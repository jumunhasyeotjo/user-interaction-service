package com.jumunhasyeotjo.userinteract.user.presentation.controller;

import com.jumunhasyeotjo.userinteract.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.res.UserDetailRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/api/v1/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/hub-driver/random")
    @Operation(
        summary = "랜덤 허브 기사 조회",
        description = "허브(Hub)에 속한 기사 중 랜덤으로 한 명의 userId를 반환합니다."
    )
    public Long getRandomHubDriver() {
        return userService.getRandomHubDriver().userId();
    }

    @GetMapping("/company-driver/random/{hubId}")
    @Operation(
        summary = "특정 허브에서 랜덤 회사 기사 조회",
        description = "hubId에 속한 회사 기사 중 랜덤으로 한 명의 userId를 반환합니다."
    )
    public Long getRandomCompanyDriver(
        @Parameter(description = "허브 ID", required = true)
        @PathVariable UUID hubId
    ) {
        return userService.getRandomCompanyDriver(hubId).userId();
    }
}
