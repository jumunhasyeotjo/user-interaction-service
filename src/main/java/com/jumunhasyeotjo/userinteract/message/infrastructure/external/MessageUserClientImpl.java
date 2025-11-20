package com.jumunhasyeotjo.userinteract.message.infrastructure.external;

import com.jumunhasyeotjo.userinteract.message.application.service.UserClient;
import org.springframework.stereotype.Component;

@Component
public class MessageUserClientImpl implements UserClient {
    @Override
    public String getSlackId(Long userId) {
        return "U09SU4KPL66";
    } // 테스트용
}
