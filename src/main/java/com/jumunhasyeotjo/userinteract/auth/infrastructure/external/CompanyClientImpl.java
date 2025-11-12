package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.CompanyClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyClientImpl implements CompanyClient {
    @Override
    public boolean exist(UUID companyId) {
        return true;
    }
}
