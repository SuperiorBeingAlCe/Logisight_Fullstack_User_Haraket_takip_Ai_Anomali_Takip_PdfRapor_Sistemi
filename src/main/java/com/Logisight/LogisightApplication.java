package com.Logisight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.Logisight")
public class LogisightApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogisightApplication.class, args);
	}

}
