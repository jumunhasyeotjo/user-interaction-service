package com.jumunhasyeotjo.userinteract;

import com.library.passport.config.WebMvcConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {
	"com.jumunhasyeotjo.userinteract",
	"com.library.passport"
})
@Import(WebMvcConfig.class)
public class UserInteractApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserInteractApplication.class, args);
	}

}
