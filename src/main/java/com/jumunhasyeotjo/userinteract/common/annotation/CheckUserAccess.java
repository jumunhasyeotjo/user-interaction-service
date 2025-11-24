package com.jumunhasyeotjo.userinteract.common.annotation;

import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserAccess {
    UserRole[] allowedRoles() default {UserRole.MASTER, UserRole.HUB_MANAGER};
    boolean checkResult() default false;
}
