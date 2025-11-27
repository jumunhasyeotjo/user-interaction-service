package com.jumunhasyeotjo.userinteract.message.application.service;

import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.CompanyManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.HubManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.UserDto;

import java.util.List;
import java.util.UUID;

public interface UserClient {
    String getSlackId(Long userId);
    List<HubManagerDto> getHubManagers(UUID hubId);
    List<CompanyManagerDto> getCompanyManagers(UUID companyId);
    UserDto getUser(Long userId);
}
