package com.zz95.jungjik;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JungjikApplication {

	public static void main(String[] args) {
		SpringApplication.run(JungjikApplication.class, args);
	}

}
