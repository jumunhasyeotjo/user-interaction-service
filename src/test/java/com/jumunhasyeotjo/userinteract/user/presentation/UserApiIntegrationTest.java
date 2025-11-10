package com.jumunhasyeotjo.userinteract.user.presentation;

import com.jumunhasyeotjo.userinteract.user.domain.repository.UserRepository;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import com.jumunhasyeotjo.userinteract.user.infrastructure.repository.JpaUserRepository;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.JoinReq;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class UserApiIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JpaUserRepository userRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 후 단건 조회까지 E2E 플로우")
    void joinAndGetUserByName_EndToEndFlow() {
        // given
        JoinReq joinReq = new JoinReq(
            "홍길동",
            "password1234",
            "slack_hong",
            UserRole.MASTER.name(),
            UUID.randomUUID()
        );

        // when: 회원가입 요청
        String name =
            given()
                .contentType(ContentType.JSON)
                .body(joinReq)
                .when()
                .post("/v1/users")
                .then()
                .statusCode(201)
                .extract()
                .path("data.name");

        // then: 회원 조회 요청
        given()
            .when()
            .get("/v1/users/name/{name}", name)
            .then()
            .statusCode(200)
            .body("data.name", equalTo("홍길동"))
            .body("data.status", equalTo(UserStatus.PENDING.name()));

        // DB 검증
        var savedUser = userRepository.findByName(name).orElseThrow();
        assertThat(savedUser.getName()).isEqualTo("홍길동");
        assertThat(savedUser.getRole()).isEqualTo(UserRole.MASTER);
        assertThat(savedUser.getStatus()).isEqualTo(UserStatus.PENDING);
    }
}
