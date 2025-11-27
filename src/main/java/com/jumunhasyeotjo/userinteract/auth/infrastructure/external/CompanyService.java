package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.CompanyClient;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyService implements CompanyClient {

    private final CompanyFeignClient companyFeignClient;

    @Override
    @CircuitBreaker(name = "companyService", fallbackMethod = "existFallback")
    @Retry(name = "companyService")
    public boolean exist(UUID companyId) {
        return companyFeignClient.exist(companyId);
    }

    public boolean existFallback(UUID companyId, Throwable e) {
        throw new BusinessException(ErrorCode.COMPANY_SERVER_ERROR);
    }
}
