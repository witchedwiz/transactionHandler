package it.vdevred.n26.service;

import org.springframework.http.HttpStatus;

import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.domain.Transaction;


public interface TransactionService {

	HttpStatus registerTransaction(Transaction transaction);

	Statistic getStatistic();

}
