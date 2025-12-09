package com.jumunhasyeotjo.userinteract.auth.presentation;

import com.jumunhasyeotjo.userinteract.auth.application.AuthService;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignInReq;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignUpReq;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.SignInRes;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.SignUpRes;
import com.library.passport.entity.ApiRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "인증 관련 API(회원가입, 로그인, 토큰 재발급, 로그아웃)")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "새로운 유저를 등록합니다.")
    public ResponseEntity<ApiRes<SignUpRes>> signUp(@RequestBody SignUpReq req) {
        SignUpCommand command = new SignUpCommand(
            req.name(),
            req.password(),
            req.slackId(),
            req.role(),
            req.belong()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiRes.success(SignUpRes.from(authService.signUp(command))));
    }

    @PostMapping("/signin")
    @Operation(summary = "로그인", description = "로그인을 수행하고 토큰을 발급합니다.")
    public ResponseEntity<ApiRes<SignInRes>> signIn(@RequestBody SignInReq req) {
        SignInCommand command = new SignInCommand(
            req.name(),
            req.password()
        );
        return ResponseEntity.ok(ApiRes.success(SignInRes.from(authService.signIn(command))));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 기반으로 새로운 토큰을 발급합니다.")
    public ResponseEntity<ApiRes<SignInRes>> reissue(
        @RequestHeader("Refresh-Token") String refreshToken
    ) {
        SignInResult result = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiRes.success(SignInRes.from(result)));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "액세스/리프레시 토큰을 만료시켜 로그아웃합니다.")
    public ResponseEntity<ApiRes<Void>> logout(
        @RequestHeader("Authorization") String accessToken,
        @RequestHeader("Refresh-Token") String refreshToken
    ) {
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ApiRes.success(null));
    }
}
