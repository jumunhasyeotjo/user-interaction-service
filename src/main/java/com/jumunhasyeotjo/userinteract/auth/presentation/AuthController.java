package com.jumunhasyeotjo.userinteract.auth.presentation;

import com.jumunhasyeotjo.userinteract.auth.application.AuthService;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignInCommand;
import com.jumunhasyeotjo.userinteract.auth.application.command.SignUpCommand;
import com.jumunhasyeotjo.userinteract.auth.application.result.SignInResult;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignInReq;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.req.SignUpReq;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.SignInRes;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.SignUpRes;
import com.jumunhasyeotjo.userinteract.common.ApiRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
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
    public ResponseEntity<ApiRes<SignInRes>> signIn(@RequestBody SignInReq req) {
        SignInCommand command = new SignInCommand(
            req.name(),
            req.password()
        );
        return ResponseEntity.ok(ApiRes.success(SignInRes.from(authService.signIn(command))));
    }

    @PostMapping("/reissue")
    public ResponseEntity<ApiRes<SignInRes>> reissue(@RequestHeader("Refresh-Token") String refreshToken) {
        SignInResult result = authService.reissue(refreshToken);
        return ResponseEntity.ok(ApiRes.success(SignInRes.from(result)));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRes<Void>> logout(@RequestHeader("Authorization") String accessToken, @RequestHeader("Refresh-Token") String refreshToken) {
        authService.logout(accessToken, refreshToken);
        return ResponseEntity.ok(ApiRes.success(null));
    }
}
