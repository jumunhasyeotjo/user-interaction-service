package com.jumunhasyeotjo.userinteract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class UserInteractApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserInteractApplication.class, args);
	}

}
