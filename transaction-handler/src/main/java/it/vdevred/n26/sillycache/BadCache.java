package it.vdevred.n26.sillycache;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.domain.Transaction;

@Component
public class BadCache {

	private static final Logger log = LoggerFactory.getLogger(BadCache.class);

	private ConcurrentHashMap<Long, Statistic> statisticsMap;
	private Statistic voidStatistic = null;
	// going for simplicity as this map is regulated by a synchronized block
	private ConcurrentHashMap<Long, Long> lockMap;

	@PostConstruct
	public void init()
	{
		// should i have wanted to also go for performance, i could have used some concurrent linked structure, as those in caffeine
		// but in this context should be enough
		// usage of a linked structure would have made the purge operation faster as the order would be ensured
		// but as it's just a limited amount of quants//slots to be kept, this approach should sitll be fine
		statisticsMap = new ConcurrentHashMap<Long, Statistic>();
		voidStatistic = new Statistic(0, 0.0, null, null);
		// this is mandatory as concurrent hashmap and similar lacks lock on most "retrival" operation
		lockMap = new ConcurrentHashMap<Long, Long>();
	}

	private Statistic createVoidStatistics()
	{
		return new Statistic(0, 0.0, null, null);
	}

	public HttpStatus store(Transaction aTransaction)
	{
		if (aTransaction != null && aTransaction.getTimestamp() != null && aTransaction.getAmount() != null && aTransaction.getAmount() != Double.NaN)
		{
			Duration withinTimeframe = Duration.of(60, ChronoUnit.SECONDS);
			if ( aTransaction != null &&  System.currentTimeMillis() - aTransaction.getTimestamp() <= withinTimeframe.toMillis())
			{
				if (update(aTransaction))
				{
					return HttpStatus.CREATED;
				}
				else
				{
					return HttpStatus.INTERNAL_SERVER_ERROR;
				}

			}
			else
			{
				return HttpStatus.NO_CONTENT;
			}
		}
		else
		{
			return HttpStatus.NOT_ACCEPTABLE;
		}
	}


	private Instant delegatedQuant(Long timeStamp, boolean roundUp)
	{
		if (timeStamp == null)
		{
			return null;
		}
		else
		{
			Instant quantInstant = Instant.ofEpochMilli(timeStamp).truncatedTo(ChronoUnit.SECONDS);
			if (roundUp == true)
			{
				// round up to next quant
				// a transaction is only relevant for the next full quant, so even a single ms after the beginnign of seconds 3, means that it lands in quant 4
				if ((timeStamp).equals(quantInstant.toEpochMilli()) == false)
				{
					quantInstant = Instant.ofEpochMilli(timeStamp).truncatedTo(ChronoUnit.SECONDS).plusSeconds(1L);
				}
				
			}
			return quantInstant;
		}
	}

	private boolean update(Transaction aTransaction)
	{
		try
		{
			long threadId = Thread.currentThread().getId();
			Instant quantInstant = delegatedQuant(aTransaction.getTimestamp(), true);
			if (quantInstant == null)
			{
				return false;
			}
			else
			{
				for (int i = 0; i <= 60; i++)
				{
					Long targetQuant = quantInstant.plusSeconds(1+i).toEpochMilli();
					try
					{
						aquirePreEmptiveLock(targetQuant, threadId);
						log.info("update locked quant "+targetQuant);
						Statistic existing = statisticsMap.get(targetQuant);
						if (existing == null)
						{
							Statistic aNewStatistic = new Statistic(1, aTransaction.getAmount(), aTransaction.getAmount(), aTransaction.getAmount());
							statisticsMap.put(targetQuant, aNewStatistic);
						}
						else
						{
							existing = existing.register(aTransaction.getAmount());
							statisticsMap.put(targetQuant, existing);
						}
					}
					finally
					{
						removePreEmptiveLock(targetQuant, threadId);
						log.info("update unlocked quant "+targetQuant);
					}
				}
				return true;
			}
		}
		catch (Exception e)
		{
			log.error("Unexpected error managing a transaction on quant for "+aTransaction.getTimestamp());
			return false;
		}
	}

	// this should not require the lock, as it will default the situation to zero if nothing is available, so if 
	// a different thread attempts to perform an update --> store operation, the concurrency has to be maintaned only between
	// two different write on the same quant
	public Statistic fetchNow()
	{
		Long now = System.currentTimeMillis();
		Long targetQuant = delegatedQuant(now, false).toEpochMilli();
		Statistic existing = statisticsMap.get(targetQuant);
		if (existing == null)
		{
			Statistic aNewStatistic = createVoidStatistics();
			// this will get null if the item is added, and an object if the object was already present
			Statistic conditional = statisticsMap.putIfAbsent(targetQuant, aNewStatistic);
			return conditional == null ? aNewStatistic : conditional;
		}
		else
		{
			return existing;
		}
	}

	// enlarge additional 60 slots for future 60 seconds to be performed every 60 seconds
	public void inflateQuants()
	{
		Long now = System.currentTimeMillis();
		Instant quantInstant = delegatedQuant(now, false);
		long threadId = Thread.currentThread().getId();
		for (int i = 0; i < 60; i++)
		{
			Long targetQuant = quantInstant.plusSeconds(1+i).toEpochMilli();
			try
			{
				aquirePreEmptiveLock(targetQuant, threadId);
				log.info("inflateQuants locked quant "+targetQuant);
				statisticsMap.putIfAbsent(targetQuant, createVoidStatistics());
			}
			finally
			{
				removePreEmptiveLock(targetQuant, threadId);
				log.info("inflateQuants unlocked quant "+targetQuant);
			}
			
		}
	}

	// reduce the quants in the semi-cache
	// this could have been much faster if the collection was also sorted...
	public void deflateQuants()
	{
		Long now = System.currentTimeMillis();
		Duration withinTimeframe = Duration.of(60, ChronoUnit.SECONDS);
		for ( Long singleInstant : statisticsMap.keySet())
		{
			if (now - singleInstant > withinTimeframe.toMillis())
			{
				//purge
				statisticsMap.remove(singleInstant);
			}
		}
	}


	/*
	 *  these methods are necessary as
	 *  https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html
	 *  Retrieval operations (including get) generally do not block, so may overlap with update operations (including put and remove)
	 *  effectively the following block is the real bottleneck of the application
	 */
	/**
	 * 
	 * @param transactionTimeStamp quant of interest, meaning that the millisecond information in the long should be always 
	 * 000 for the millisecond part and truncate effectively to the next desiderable full second
	 * @param ownwerThread threadid of the thread
	 */
	private synchronized void aquirePreEmptiveLock(long transactionTimeStamp, long ownwerThread)
	{
		boolean aquired = false;
		while (aquired == false)
		{
			Long result = lockMap.putIfAbsent(transactionTimeStamp, ownwerThread);
			// object was not available, has been added
			if (result == null)
			{
				aquired = true;
			}
		}
	}

	private synchronized void removePreEmptiveLock(long transactionTimeStamp, long ownwerThread)
	{
		Long persisted = lockMap.get(transactionTimeStamp);
		if (persisted != null && persisted.equals(ownwerThread))
		{
			lockMap.remove(transactionTimeStamp, ownwerThread);
		}
	}

	// this is kinda rough, but i suppose it will make do..
	// in the real world, should i really have wanted to do something along those lines, would also have the detail of
	// when the lock was established and add a solid purge job policy based for locks based on age of the lock, but it will have to do
	public void removeOrphans() {
		Instant targetQuant = delegatedQuant(System.currentTimeMillis(), false);
		Duration withinTimeframe = Duration.of(600, ChronoUnit.SECONDS);
		for ( Long singleInstant : lockMap.keySet())
		{
			if (targetQuant.toEpochMilli() - singleInstant > withinTimeframe.toMillis())
			{
				if (statisticsMap.get(singleInstant) == null)
				{
					statisticsMap.remove(singleInstant);
				}
			}
		}

	}
}
