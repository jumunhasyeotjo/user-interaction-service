package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.HubClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "hub-service",
    url = "lb://hub-product-stock-company"
)
public interface HubFeignClient {
    @GetMapping("/api/v1/hubs/exist/{hubId}")
    boolean exist(@PathVariable("hubId") UUID hubId);
}
