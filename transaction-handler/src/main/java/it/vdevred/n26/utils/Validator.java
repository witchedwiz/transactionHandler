package it.vdevred.n26.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import it.vdevred.n26.exceptions.OldTransactionException;

public class Validator {
	
	/**
	 * 
	 * @param instantValue --> the instant to fit
	 * @param maxInterestedAge --> express in seconds the maximum timewindow of interest
	 * @return
	 */
	public static boolean validateForInterval(Long instantValue, int maxInterestedAge) throws OldTransactionException
	{
		
		Duration withinTimeframe = Duration.of(maxInterestedAge, ChronoUnit.SECONDS);
		if (instantValue == null || System.currentTimeMillis() - instantValue > withinTimeframe.toMillis())
		{
			throw new OldTransactionException("incomplete or irrelevant for statistics");
		}
		return true;
	}

}
