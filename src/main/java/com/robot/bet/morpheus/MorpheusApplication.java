package com.robot.bet.morpheus;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableRabbit
@SpringBootApplication
@EnableAsync
//@ComponentScan(basePackages = {"com.robot.bet.morpheus.controller"})
public class MorpheusApplication {

	public static void main(String[] args) {
		
        //new SpringApplicationBuilder(MorpheusApplication.class).web(WebApplicationType.NONE).run(args);
		//SpringApplication.run(MorpheusApplication.class, args);
		
		SpringApplication application = new SpringApplication(MorpheusApplication.class);
		application.setWebApplicationType(WebApplicationType.NONE);
		application.run(args);
	}

}
