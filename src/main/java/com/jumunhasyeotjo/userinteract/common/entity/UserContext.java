package com.jumunhasyeotjo.userinteract.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserContext {
    private Long userId;
    private String name;
    private String role;
}
