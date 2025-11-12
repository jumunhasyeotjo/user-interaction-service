package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.JoinReq;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserClientImpl implements UserClient {
    private final UserService userService;

    @Override
    public UserDto join(JoinReq req) {
        return UserDto.from(userService.join(req.toCommand()));
    }

    @Override
    public UserDto validate(String name, String password) {
         return UserDto.from(userService.validatePassword(name, password));
    }
}
