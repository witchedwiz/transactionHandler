package it.vdevred.n26;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;

import it.vdevred.n26.service.TransactionService;
import it.vdevred.n26.service.impl.TransactionServiceImpl;
import it.vdevred.n26.sillycache.BadCache;
import it.vdevred.n26.sillycache.CacheManagement;

@SpringBootTest
@RunWith(SpringRunner.class)
@ComponentScan("it.vdevred.n26")
public class ApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void shouldCreateApplication() throws Exception {
        assertThat(context, notNullValue());
    }
    
    @Bean
	public CacheManagement cacheManagement() {
		return new CacheManagement();
	}
    
    @Bean
	public BadCache badCache() {
		return new BadCache();
	}
    
    @Bean
	public TransactionService transactionService() {
		return new TransactionServiceImpl();
	}

}