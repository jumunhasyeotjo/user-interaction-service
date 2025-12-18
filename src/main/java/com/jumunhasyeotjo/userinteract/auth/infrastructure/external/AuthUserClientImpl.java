package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.JoinReq;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDetailDto;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUserClientImpl implements UserClient {
    private final UserService userService;

    @Override
    public UserDto join(JoinReq req) {
        return UserDto.from(userService.join(req.toCommand()));
    }

    @Override
    public UserDetailDto validate(String name, String password) {
         return UserDetailDto.from(userService.validatePassword(name, password));
    }
}
