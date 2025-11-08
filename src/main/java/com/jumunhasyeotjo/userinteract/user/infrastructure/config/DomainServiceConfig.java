package com.jumunhasyeotjo.userinteract.user.infrastructure.config;

import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.service.UserDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceConfig {
    @Bean
    public UserDomainService userDomainService(UserRepository userRepository) {
        return new UserDomainService(userRepository);
    }
}
