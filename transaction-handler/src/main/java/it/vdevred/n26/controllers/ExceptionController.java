package it.vdevred.n26.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import it.vdevred.n26.exceptions.OldTransactionException;


@ControllerAdvice
public class ExceptionController {
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException() {
        // Nothing to do
    }
	
	@ResponseStatus(HttpStatus.NO_CONTENT)  // 204
    @ExceptionHandler(OldTransactionException.class)
    public void handleOldTransactionException() {
        // Nothing to do
    }

}
