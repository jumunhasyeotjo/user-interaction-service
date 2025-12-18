package com.jumunhasyeotjo.userinteract.auth.application.service;

import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.JoinReq;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDetailDto;
import com.jumunhasyeotjo.userinteract.auth.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;

public interface UserClient {
    UserDto join(JoinReq req);
    UserDetailDto validate(String name, String password);
}
