package com.jumunhasyeotjo.userinteract.common.aop;

import com.jumunhasyeotjo.userinteract.common.ApiRes;
import com.jumunhasyeotjo.userinteract.common.annotation.CheckUserAccess;
import com.jumunhasyeotjo.userinteract.common.entity.UserContext;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
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
public class UserAuthorizationAspect {

    @Around("@annotation(checkUserAccess)")
    public Object authorize(ProceedingJoinPoint joinPoint, CheckUserAccess checkUserAccess) throws Throwable {
        List<UserRole> allowedRoles = Arrays.asList(checkUserAccess.allowedRoles());
        boolean checkResult = checkUserAccess.checkResult();
        UserContext userContext = null;

        for (Object arg : joinPoint.getArgs()) {
            if (arg instanceof UserContext uc) userContext = uc;
        }

        if (userContext == null) {
            throw new BusinessException(ErrorCode.MISSING_AUTH_HEADERS);
        }

        if (!checkResult) {
            authorizeWithoutResult(userContext.getRole(), allowedRoles);
            return joinPoint.proceed();
        } else {
            Object result = joinPoint.proceed();
            Long resultId = extractUserIdFromResult(result);
            authorizeWithResult(
                userContext.getUserId(),
                resultId,
                userContext.getRole(),
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
