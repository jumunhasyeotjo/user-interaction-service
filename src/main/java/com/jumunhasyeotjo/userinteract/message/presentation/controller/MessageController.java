package com.jumunhasyeotjo.userinteract.message.presentation.controller;

import com.jumunhasyeotjo.userinteract.common.ApiRes;
import com.jumunhasyeotjo.userinteract.common.annotation.CheckUserAccess;
import com.jumunhasyeotjo.userinteract.common.entity.UserContext;
import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.req.MessageCreateReq;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.res.MessageRes;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/messages")
public class MessageController {
    private final MessageService messageService;

    @PostMapping()
    public ResponseEntity<ApiRes<Void>> createMessage(@RequestBody MessageCreateReq req) {
        messageService.createMessage(req.toCommand());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiRes.success(null));
    }

    @GetMapping("/{messageId}")
    @CheckUserAccess(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_DRIVER},
        checkResult = true
    )
    public ResponseEntity<ApiRes<MessageRes>> getMessage(UserContext userContext, @PathVariable UUID messageId) {
        return ResponseEntity.ok(
            ApiRes.success(
                MessageRes.from(messageService.getMessage(messageId))
            )
        );
    }

    @GetMapping("/user/{userId}")
    @CheckUserAccess(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_DRIVER}
    )
    public ResponseEntity<ApiRes<Page<MessageRes>>> getMessages(
        UserContext userContext,
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
