package it.vdevred.n26.controllers;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import it.vdevred.n26.ApplicationTest;
import it.vdevred.n26.domain.Transaction;
import it.vdevred.n26.exceptions.OldTransactionException;
import it.vdevred.n26.service.TransactionService;
import it.vdevred.n26.service.impl.TransactionServiceImpl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes= ApplicationTest.class)
public class TransactionControllerTest {
	
	@Autowired
    WebApplicationContext webApplicationContext;

    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;


    @Before
    public void setUp()
    {
    	mvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }


    @Test
    public void shouldValidateRequest() throws Exception {
        mvc.perform(post("/api/transactions")
                .contentType("application/json")
                .content("{\"timestamp\": 0}"))
                .andExpect(status().isNoContent())
                .andExpect(content().bytes(new byte[0]));

       verifyZeroInteractions(transactionService);
    }

    @Test
    public void shouldHandleIrrelevantTimestampException() throws Exception {
        doThrow(new OldTransactionException("old"))
                .when(transactionService).registerTransaction(any(Transaction.class));

        mvc.perform(post("/api/transactions")
                .contentType("application/json")
                .content("{\"amount\": 12.3,\"timestamp\": 1467872000234}"))
                .andExpect(status().isNoContent())
                .andExpect(content().bytes(new byte[0]));
    }
}