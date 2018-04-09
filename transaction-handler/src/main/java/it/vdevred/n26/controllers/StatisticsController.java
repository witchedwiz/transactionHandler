package it.vdevred.n26.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.service.TransactionService;

@RestController
public class StatisticsController {

	@Autowired
    private TransactionService transactionService;

	//technically could have gone with mapping and requestmapping but let's go the short route
	@RequestMapping("/api/statistics")
	@GetMapping(produces = "application/json")
    public Statistic getTransactionStatistics() {
        return transactionService.getStatistic();
    }
}