package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "hub-product-stock-company")
public interface HubCompanyFeignClient {

    @GetMapping("/internal/api/v1/hubs/{hubId}/exists")
    Object existHub(@PathVariable("hubId") UUID hubId);

    @GetMapping("/internal/api/v1/companies/{companyId}/exists")
    Object existCompany(@PathVariable("companyId") UUID companyId);
}
