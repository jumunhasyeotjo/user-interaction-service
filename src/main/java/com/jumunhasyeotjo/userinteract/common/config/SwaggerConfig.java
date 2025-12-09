package com.jumunhasyeotjo.userinteract.common.config;

import com.library.passport.proto.PassportProto;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(PassportProto.Passport.class);
    }

    @Bean
    public OpenAPI openAPI() {
        String passportSchemeName = "passportHeader";

        Schema<?> pageSchema = new Schema<>()
            .addProperty("content", new ArraySchema())
            .addProperty("pageable", new ObjectSchema())
            .addProperty("totalElements", new IntegerSchema())
            .addProperty("totalPages", new IntegerSchema())
            .addProperty("size", new IntegerSchema())
            .addProperty("number", new IntegerSchema());

        // Passport (Custom Header) 설정 - 핵심 부분
        SecurityScheme passportHeader = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY)
            .in(SecurityScheme.In.HEADER)
            .name("X-Passport") // 실제 헤더 키 값 (Gateway와 일치시켜야 함)
            .description("Gateway에서 전달받는 사용자 정보 (JSON String)");

        // 2. Security Requirement 정의 (전역 적용)
        SecurityRequirement securityRequirement = new SecurityRequirement()
            .addList(passportSchemeName);

        // 3. OpenAPI 객체 생성 및 반환
        return new OpenAPI()
            .info(new Info()
                .title("User-Interaction API")
                .description("User-Interaction 서비스 API 명세서")
                .version("v1.0.0"))
            .components(new Components().addSchemas("Page", pageSchema))
            .components(new Components()
                .addSecuritySchemes(passportSchemeName, passportHeader))
            .addSecurityItem(securityRequirement);
    }
}
