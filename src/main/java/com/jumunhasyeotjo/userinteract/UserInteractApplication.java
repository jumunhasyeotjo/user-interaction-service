package com.jumunhasyeotjo.userinteract;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class UserInteractApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserInteractApplication.class, args);
	}

}
