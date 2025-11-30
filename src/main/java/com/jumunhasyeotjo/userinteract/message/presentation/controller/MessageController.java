package com.jumunhasyeotjo.userinteract.message.presentation.controller;

import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.res.MessageRes;
import com.library.passport.annotation.PassportAuthorize;
import com.library.passport.annotation.PassportUser;
import com.library.passport.entity.ApiRes;
import com.library.passport.entity.PassportUserRole;
import com.library.passport.proto.PassportProto.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @GetMapping("/{messageId}")
    @PassportAuthorize(
        allowedRoles = {PassportUserRole.MASTER, PassportUserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<MessageRes>> getMessage(@PassportUser Passport passport, @PathVariable UUID messageId) {
        return ResponseEntity.ok(
            ApiRes.success(
                MessageRes.from(messageService.getMessage(messageId))
            )
        );
    }

    @GetMapping("/user/{userId}")
    @PassportAuthorize(
        allowedRoles = {PassportUserRole.MASTER, PassportUserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<Page<MessageRes>>> getMessages(
        @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                messageService.getMessagesByUserId(userId, pageable).map(MessageRes::from)
            )
        );
    }
}
