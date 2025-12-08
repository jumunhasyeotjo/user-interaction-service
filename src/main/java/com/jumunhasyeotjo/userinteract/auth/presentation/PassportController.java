package com.jumunhasyeotjo.userinteract.auth.presentation;

import com.jumunhasyeotjo.userinteract.auth.application.PassportService;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.PassportRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/passports")
@RequiredArgsConstructor
@Tag(name = "Passport API", description = "Passport 발급 관련 API")
public class PassportController {

    private final PassportService passportService;

    @PostMapping
    @Operation(
        summary = "Passport 발급",
        description = "JWT를 기반으로 Passport를 발급합니다."
    )
    public PassportRes issuePassport(@RequestParam("jwt") String jwt) {
        return PassportRes.fromPassportResult(passportService.issuePassport(jwt));
    }
}
