package it.vdevred.n26.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenericController {

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

}