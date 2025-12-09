package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumunhasyeotjo.userinteract.auth.application.service.HubCompanyClient;
import com.jumunhasyeotjo.userinteract.common.error.BusinessException;
import com.jumunhasyeotjo.userinteract.common.error.ErrorCode;
import com.library.passport.entity.ApiRes;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HubCompanyService implements HubCompanyClient {

    private final HubCompanyFeignClient hubCompanyFeignClient;
    private final ObjectMapper objectMapper;

    @Override
    @CircuitBreaker(name = "hubService", fallbackMethod = "existHubFallback")
    @Retry(name = "hubService")
    public Boolean existHub(UUID hubId) {
        Object response = hubCompanyFeignClient.existHub(hubId);

        Map<String, Object> temp = objectMapper.convertValue(response, new TypeReference<Map<String, Object>>() {});
        Map<String, Boolean> data = objectMapper.convertValue(temp.get("data"), new TypeReference<Map<String, Boolean>>() {});
        return data.get("exist");
    }


    @CircuitBreaker(name = "companyService", fallbackMethod = "existCompanyFallback")
    @Retry(name = "companyService")
    public Boolean existCompany(UUID companyId) {
        Object response = hubCompanyFeignClient.existCompany(companyId);

        Map<String, Object> temp = objectMapper.convertValue(response, new TypeReference<Map<String, Object>>() {});
        Map<String, Boolean> data = objectMapper.convertValue(temp.get("data"), new TypeReference<Map<String, Boolean>>() {});
        return data.get("exist");
    }

    // fallback 메서드는 원본 메서드와 동일한 파라미터 + Throwable
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