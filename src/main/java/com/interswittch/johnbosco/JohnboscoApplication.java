package com.interswittch.johnbosco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class JohnboscoApplication {

	public static void main(String[] args) {
		SpringApplication.run(JohnboscoApplication.class, args);
	}

}
