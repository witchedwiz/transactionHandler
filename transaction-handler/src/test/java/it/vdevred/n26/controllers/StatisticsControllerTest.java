package it.vdevred.n26.controllers;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import it.vdevred.n26.ApplicationTest;
import it.vdevred.n26.domain.Statistic;
import it.vdevred.n26.service.TransactionService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
//@WebMvcTest(StatisticsController.class)
@AutoConfigureMockMvc
@SpringBootTest(classes= ApplicationTest.class)
public class StatisticsControllerTest {
	
	@Autowired
    WebApplicationContext webApplicationContext;

    //@Autowired
    private MockMvc mvc;

    @MockBean
    private TransactionService transactionService;

    @Before
    public void setUp()
    {
    	mvc=MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }
    

    @Test
    public void shouldReturnSampleStatistics() throws Exception {
    	Statistic aStatistic = new Statistic();
    	aStatistic.setCount(5);
    	aStatistic.setMin(6.0);
    	aStatistic.setMax(10.0);
    	aStatistic.setSum(25.0);
    	aStatistic.computeAvg();
        when(transactionService.getStatistic()).thenReturn(aStatistic);

        mvc.perform(get("/api/statistics").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("count", is(5)))
                .andExpect(jsonPath("min", is(6.0)))
                .andExpect(jsonPath("max", is(10.0)))
                .andExpect(jsonPath("sum", is(25.0)))
                .andExpect(jsonPath("avg", is(5.0)));
    }

    @Test
    public void shouldReturnZeroStatistics() throws Exception {
    	//could have gone with double.Nan but well this is good enough for a concept demo
    	Statistic voidStatistic = new Statistic(0, 0.0, null, null);
        when(transactionService.getStatistic()).thenReturn(voidStatistic);

        mvc.perform(get("/api/statistics").accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("count", is(0)))
                .andExpect(jsonPath("sum", is(0.0)))
                .andExpect(jsonPath("avg", org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("max", org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("min", org.hamcrest.Matchers.nullValue()));
    }
}