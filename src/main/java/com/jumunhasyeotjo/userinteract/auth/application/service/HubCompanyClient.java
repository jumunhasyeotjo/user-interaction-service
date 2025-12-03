package com.jumunhasyeotjo.userinteract.auth.application.service;

import java.util.UUID;

public interface HubCompanyClient {
    boolean existHub(UUID hubId);
    boolean existCompany(UUID companyId);
}
