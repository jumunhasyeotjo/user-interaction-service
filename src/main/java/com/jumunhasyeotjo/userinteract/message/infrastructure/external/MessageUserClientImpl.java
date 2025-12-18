package com.jumunhasyeotjo.userinteract.message.infrastructure.external;

import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.CompanyManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.HubManagerDto;
import com.jumunhasyeotjo.userinteract.message.infrastructure.dto.UserDto;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MessageUserClientImpl implements UserClient {

    private final UserService userService;

    @Override
    public String getSlackId(Long userId) {
        return userService.getUser(userId).slackId();
    } // 테스트용

    @Override
    public List<HubManagerDto> getHubManagers(UUID hubId) {
        return userService.getHubManagerByHubId(hubId)
            .stream().map(HubManagerDto::fromResult).toList();
    }

    @Override
    public List<CompanyManagerDto> getCompanyManagers(UUID companyId) {
        return userService.getCompanyManagerByCompanyId(companyId)
            .stream().map(CompanyManagerDto::fromResult).toList();
    }

    @Override
    public UserDto getUser(Long userId) {
        return UserDto.fromResult(userService.getUser(userId));
    }
}
