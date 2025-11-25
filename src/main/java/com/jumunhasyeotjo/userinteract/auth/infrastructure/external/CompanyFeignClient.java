package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.CompanyClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "company-service",
    url = "http://localhost:8087"
)
public interface CompanyFeignClient extends CompanyClient {
    @Override
    @GetMapping("/api/v1/companies/exist/{companyId}")
    boolean exist(@PathVariable("companyId") UUID companyId);
}
