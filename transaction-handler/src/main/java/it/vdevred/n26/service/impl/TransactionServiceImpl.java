package it.vdevred.n26.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.domain.Transaction;
import it.vdevred.n26.service.TransactionService;
import it.vdevred.n26.sillycache.BadCache;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {
	
	@Autowired
	private BadCache badCache;	 
	 
	@Override
	public HttpStatus registerTransaction(Transaction transaction)
	{
		if (badCache == null)
		{
			return HttpStatus.CONTINUE;
		}
		return badCache.store(transaction);
	}
	
	@Override
	public Statistic getStatistic()
	{
		return badCache.fetchNow();
	}

}
