package com.jumunhasyeotjo.userinteract.auth.application.service;

import com.library.passport.entity.ApiRes;

import java.util.UUID;

public interface HubCompanyClient {
    Boolean existHub(UUID hubId);
    Boolean existCompany(UUID companyId);
}
