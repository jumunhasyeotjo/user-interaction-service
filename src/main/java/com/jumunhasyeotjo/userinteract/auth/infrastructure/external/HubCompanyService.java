package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.HubCompanyClient;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubCompanyService implements HubCompanyClient {

    private final HubCompanyFeignClient hubCompanyFeignClient;

    @Override
    @CircuitBreaker(name = "hubService", fallbackMethod = "existFallback")
    @Retry(name = "hubService")
    public boolean existHub(UUID companyId) {
        return hubCompanyFeignClient.existHub(companyId).getData().get("exist");
    }

    @Override
    @CircuitBreaker(name = "companyService", fallbackMethod = "existFallback")
    @Retry(name = "companyService")
    public boolean existCompany(UUID companyId) {
        return hubCompanyFeignClient.existCompany(companyId).getData().get("exist");
    }

    public boolean existFallback(UUID companyId, Throwable e) {
        throw new BusinessException(ErrorCode.HUBCOMPANY_SERVER_ERROR);
    }
}
