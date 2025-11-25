package com.jumunhasyeotjo.userinteract.common.config;

import com.jumunhasyeotjo.userinteract.common.entity.UserContext;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class UserContextResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(UserContext.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        String idHeader = request.getHeader("X-User-Id");
        String nameHeader = request.getHeader("X-User-Name");
        String roleHeader = request.getHeader("X-User-Role");

        if (idHeader == null || roleHeader == null || nameHeader == null) {
            throw new BusinessException(ErrorCode.MISSING_AUTH_HEADERS);
        }

        return new UserContext(Long.parseLong(idHeader), nameHeader, roleHeader);
    }
}
