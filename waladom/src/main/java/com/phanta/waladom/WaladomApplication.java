package com.phanta.waladom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WaladomApplication {

	private static final Logger logger = LoggerFactory.getLogger(WaladomApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(WaladomApplication.class, args);
		logger.info("Waladom application is running....");
	}
}
