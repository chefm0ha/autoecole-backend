package com.springBoot.autoEcole;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class AutoEcoleApplication {

	public static void main(String[] args) {
		SpringApplication.run(AutoEcoleApplication.class, args);
	}

}
