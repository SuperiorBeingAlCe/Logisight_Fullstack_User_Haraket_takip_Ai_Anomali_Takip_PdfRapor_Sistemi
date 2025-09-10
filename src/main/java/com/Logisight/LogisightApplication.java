package com.Logisight;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.Logisight.security.JwtProperties;

@SpringBootApplication
@ComponentScan(basePackages = "com.Logisight")
@EnableCaching
@EnableConfigurationProperties(JwtProperties.class)
@EnableScheduling
public class LogisightApplication {

	public static void main(String[] args) { 
		SpringApplication.run(LogisightApplication.class, args);
	}

}
