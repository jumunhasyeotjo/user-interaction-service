package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.HubClient;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubService implements HubClient {

    private final HubFeignClient hubFeignClient;

    @Override
    @CircuitBreaker(name = "hubService", fallbackMethod = "existFallback")
    @Retry(name = "hubService")
    public boolean exist(UUID companyId) {
        return hubFeignClient.exist(companyId);
    }

    public boolean existFallback(UUID companyId, Throwable e) {
        throw new BusinessException(ErrorCode.HUB_SERVER_ERROR);
    }
}
