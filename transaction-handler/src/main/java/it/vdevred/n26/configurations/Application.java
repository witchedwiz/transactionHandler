package it.vdevred.n26.configurations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("it.vdevred.n26")
public class Application {


	public static void main(String[] args) {
		ConfigurableApplicationContext anObj = SpringApplication.run(Application.class, args);
	}

	//    @Bean
	//    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
	//        return args -> {
	//
	//            System.out.println("Let's inspect the beans provided by Spring Boot:");
	//
	//            String[] beanNames = ctx.getBeanDefinitionNames();
	//            Arrays.sort(beanNames);
	//            for (String beanName : beanNames) {
	//                System.out.println(beanName);
	//            }
	//
	//        };
	//    }

}