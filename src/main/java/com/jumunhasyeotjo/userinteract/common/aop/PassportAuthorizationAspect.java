package com.jumunhasyeotjo.userinteract.common.aop;

import com.jumunhasyeotjo.userinteract.common.ApiRes;
import com.jumunhasyeotjo.userinteract.common.annotation.PassportAuthorize;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.library.passport.proto.PassportProto.Passport;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PassportAuthorizationAspect {

    @Around("@annotation(passportAuthorize)")
    public Object around(ProceedingJoinPoint joinPoint, PassportAuthorize passportAuthorize) throws Throwable {
        List<UserRole> allowedRoles = Arrays.asList(passportAuthorize.allowedRoles());
        boolean checkResult = passportAuthorize.checkResult();
        Passport passport = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof Passport pp) passport = pp;
        }

        if (passport == null) {
            throw new BusinessException(ErrorCode.MISSING_AUTH_HEADERS);
        }

        Long userId = passport.getUserId();
        String role = passport.getRole();

        if (!checkResult) {
            authorizeWithoutResult(role, allowedRoles);
            return joinPoint.proceed();
        } else {
            Object result = joinPoint.proceed();
            Long resultId = extractUserIdFromResult(result);
            authorizeWithResult(
                userId,
                resultId,
                role,
                allowedRoles
            );

            return result;
        }
    }

    private void authorizeWithoutResult(String requestRole, List<UserRole> allowedRoles) {
        if (!allowedRoles.contains(UserRole.of(requestRole))) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
    }

    private void authorizeWithResult(Long requestId, Long resultId, String requestRole, List<UserRole> allowedRoles) {
        if (!allowedRoles.contains(UserRole.of(requestRole))) {
            if (!requestId.equals(resultId)) {
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
        }
    }

    private Long extractUserIdFromResult(Object result) {
        try {
            if (result instanceof ResponseEntity<?> responseEntity) {
                Object body = responseEntity.getBody();
                if (body instanceof ApiRes<?> apiRes) {
                    Object data = apiRes.getData();
                    Method method = data.getClass().getMethod("userId");
                    return (Long) method.invoke(data);
                }
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
