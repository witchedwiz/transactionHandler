package it.vdevred.n26.controllers;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.vdevred.n26.domain.Transaction;
import it.vdevred.n26.service.TransactionService;
import it.vdevred.n26.utils.Validator;

@RestController
public class TransactionController {


	@Autowired
	private TransactionService transactionService;

	// enforcing only post with less boilerplate
	@RequestMapping("/api/transactions")
	@PostMapping(consumes = "application/json")
	public void registerTransaction(@Valid @RequestBody Transaction transaction, HttpServletResponse response) {
		Validator.validateForInterval(transaction.getTimestamp(), 60);
		HttpStatus status = transactionService.registerTransaction(transaction);
		response.setStatus(status.value());
	}
}