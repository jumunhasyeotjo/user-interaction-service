package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "hubService")
    @Retry(name = "hubService", fallbackMethod = "existHubFallback")
    public Boolean existHub(UUID hubId) {
        return hubCompanyFeignClient.existHub(hubId).data().get("exist");
    }


    @CircuitBreaker(name = "companyService")
    @Retry(name = "companyService", fallbackMethod = "existCompanyFallback")
    public Boolean existCompany(UUID companyId) {
        return hubCompanyFeignClient.existCompany(companyId).data().get("exist");
    }

    public Boolean existHubFallback(UUID hubId, Throwable e) {
        System.err.println("existHubFallback triggered for hubId: " + hubId);
        e.printStackTrace();
        throw new BusinessException(ErrorCode.HUBCOMPANY_SERVER_ERROR);
    }

    public Boolean existCompanyFallback(UUID companyId, Throwable e) {
        System.err.println("existCompanyFallback triggered for companyId: " + companyId);
        e.printStackTrace();
        throw new BusinessException(ErrorCode.HUBCOMPANY_SERVER_ERROR);
    }
}