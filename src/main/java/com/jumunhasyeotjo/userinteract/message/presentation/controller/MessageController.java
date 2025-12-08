package com.jumunhasyeotjo.userinteract.message.presentation.controller;

import com.jumunhasyeotjo.userinteract.message.application.MessageService;
import com.jumunhasyeotjo.userinteract.message.presentation.dto.res.MessageRes;
import com.library.passport.annotation.PassportAuthorize;
import com.library.passport.annotation.PassportUser;
import com.library.passport.entity.ApiRes;
import com.library.passport.entity.PassportUserRole;
import com.library.passport.proto.PassportProto.Passport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/messages")
@Tag(name = "Message API", description = "메시지 조회 API")
public class MessageController {

    private final MessageService messageService;

    @Operation(
        summary = "메시지 단건 조회",
        description = "messageId에 해당하는 메시지를 조회합니다.",
        security = { @SecurityRequirement(name = "passportHeader") }
    )
    @GetMapping("/{messageId}")
    @PassportAuthorize(
        allowedRoles = {PassportUserRole.MASTER, PassportUserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<MessageRes>> getMessage(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @Parameter(description = "메시지 ID") @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                MessageRes.from(messageService.getMessage(messageId))
            )
        );
    }

    @Operation(
        summary = "사용자 메시지 목록 조회",
        description = "특정 userId가 받은 메시지 목록을 조회합니다. Pageable 지원",
        security = { @SecurityRequirement(name = "passportHeader") }
    )
    @GetMapping("/user/{userId}")
    @PassportAuthorize(
        allowedRoles = {PassportUserRole.MASTER, PassportUserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<Page<MessageRes>>> getMessages(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable,
        @Parameter(description = "사용자 ID") @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                messageService.getMessagesByUserId(userId, pageable)
                    .map(MessageRes::from)
            )
        );
    }
}