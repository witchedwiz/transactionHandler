package it.vdevred.n26.service.impl;

import static org.junit.Assert.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import it.vdevred.n26.ApplicationTest;
import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.domain.Transaction;
import it.vdevred.n26.service.TransactionService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= ApplicationTest.class)
public class TransactionServiceImplTest {

	@Autowired
	private TransactionService transactionService;
	
	@Test
    public void statisticCalculatedAccordingly() throws Exception {
		long almostNow = Instant.ofEpochMilli(System.currentTimeMillis()).truncatedTo(ChronoUnit.SECONDS).minusSeconds(15L).toEpochMilli();
		Transaction aTransaction = new Transaction(4.0, almostNow);
		transactionService.registerTransaction(aTransaction);
		Statistic targetStatistic = transactionService.getStatistic();
		//individual to pinpoint inconsistency
		assertTrue(targetStatistic != null);
		assertTrue(targetStatistic.getMin() == 4.0);
		assertTrue(targetStatistic.getMax() == 4.0);
		assertTrue(targetStatistic.getAvg() == 4.0);
		assertTrue(targetStatistic.getCount() == 1L);
		Transaction anotherTransaction = new Transaction(6.0, almostNow);
		transactionService.registerTransaction(anotherTransaction);
		targetStatistic = transactionService.getStatistic();
		//inline to massively validate
		assertTrue(targetStatistic != null && targetStatistic.getMin() == 4.0 && targetStatistic.getMax() == 6.0
				&& targetStatistic.getAvg() == 5.0 && targetStatistic.getCount() == 2L);
    }


}
