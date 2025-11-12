package com.jumunhasyeotjo.userinteract.auth.infrastructure.external;

import com.jumunhasyeotjo.userinteract.auth.application.service.HubClient;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class HubClientImpl implements HubClient {
    @Override
    public boolean exist(UUID hubId) {
        return true;
    }
}
