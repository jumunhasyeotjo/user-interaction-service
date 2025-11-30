package com.jumunhasyeotjo.userinteract.common.config;

import com.library.passport.config.WebMvcConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test") // test 프로파일에서는 로드되지 않음
@ComponentScan(basePackages = "com.library.passport")
@Import(WebMvcConfig.class)
public class PassportWebConfig {
}
