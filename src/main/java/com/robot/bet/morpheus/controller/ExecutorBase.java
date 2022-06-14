package com.robot.bet.morpheus.controller;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExecutorBase implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ExecutorBase.class);

    private Boolean debug = true;
    
    @PostConstruct
    public void atStartup() {
        if (debug) {
            log.warn("###### Startup ok");
        }
    }

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		while(true) {
			
		}
	}
}