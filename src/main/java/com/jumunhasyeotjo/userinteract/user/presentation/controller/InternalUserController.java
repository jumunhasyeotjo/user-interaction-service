package com.jumunhasyeotjo.userinteract.user.presentation.controller;

import com.jumunhasyeotjo.userinteract.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/users")
@RequiredArgsConstructor
public class InternalUserController {
    private final UserService userService;

    @GetMapping("/hub-driver/random")
    public Long getRandomHubDriver() {
        return userService.getRandomHubDriver().userId();
    }

    @GetMapping("/company-driver/random/{hubId}")
    public Long getRandomCompanyDriver(@PathVariable UUID hubId) {
        return userService.getRandomCompanyDriver(hubId).userId();
    }
}
