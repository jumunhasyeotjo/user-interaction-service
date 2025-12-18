package com.jumunhasyeotjo.userinteract.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class GlobalControllerLogAspect {

    // 패키지 범위는 프로젝트 상황에 맞게 조정
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @Around("controllerPointcut()")
    public Object logRequestResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = (attributes != null) ? attributes.getRequest() : null;

        String method = (request != null) ? request.getMethod() : "UNKNOWN";
        String uri = (request != null) ? request.getRequestURI() : "UNKNOWN";
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        // [REQUEST] 진입 로그
        log.info(">>> [Controller Request] Method=[{}] URI=[{}] Handler=[{}.{}]",
                method, uri, className, methodName);

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("<<< [Controller Exception] Method=[{}] URI=[{}] Duration=[{}ms] Exception=[{}]",
                    method, uri, duration, e.getMessage());
            throw e;
        }

        // [RESPONSE] 종료 로그
        long duration = System.currentTimeMillis() - startTime;
        log.info("<<< [Controller Response] Method=[{}] URI=[{}] Duration=[{}ms]",
                method, uri, duration);

        return result;
    }
}