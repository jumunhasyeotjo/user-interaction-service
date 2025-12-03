package com.jumunhasyeotjo.userinteract.message.presentation.controller;

import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.req.MessageCreateReq;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.req.ShippingMessageCreateReq;
import com.library.passport.entity.ApiRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/messages")
public class InternalMessageController {
    private final MessageService messageService;

    @PostMapping("/create-message")
    public ResponseEntity<ApiRes<Void>> createMessage(@RequestBody MessageCreateReq req) {
        messageService.createMessage(req.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiRes.success(null));
    }

    @PostMapping("/create-shipping-message")
    public ResponseEntity<ApiRes<Void>> createShippingMessage(@RequestBody ShippingMessageCreateReq req) {
        messageService.createShippingMessage(req.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiRes.success(null));
    }
}
