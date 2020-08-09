package com.allanweber.candidatescareer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties
@SpringBootApplication
public class CandidatesCareerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CandidatesCareerApplication.class, args);
	}

}
