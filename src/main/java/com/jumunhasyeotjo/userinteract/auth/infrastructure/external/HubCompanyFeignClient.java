package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.library.passport.entity.ApiRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;
import java.util.UUID;

@FeignClient(
    name = "hub-product-stock-company"
)
public interface HubCompanyFeignClient {
    @GetMapping("/internal/api/v1/hubs/{hubId}/exists")
    ApiRes<Map<String, Boolean>> existHub(@PathVariable("hubId") UUID hubId);

    @GetMapping("/internal/api/v1/companies/{companyId}/exists")
    ApiRes<Map<String, Boolean>> existCompany(@PathVariable("companyId") UUID companyId);
}
