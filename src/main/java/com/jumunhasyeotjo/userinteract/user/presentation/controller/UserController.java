package com.jumunhasyeotjo.userinteract.user.presentation.controller;

import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.application.command.ApproveCommand;
import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.ApproveReq;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.res.UserDetailRes;
import com.library.passport.annotation.PassportAuthorize;
import com.library.passport.annotation.PassportUser;
import com.library.passport.entity.ApiRes;
import com.library.passport.proto.PassportProto.Passport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "passportHeader")
public class UserController {

    private final UserService userService;

    @PatchMapping("/approve")
    @PassportAuthorize
    @Operation(summary = "유저 승인 처리", description = "유저 상태를 변경(승인/거절) 처리합니다.")
    public ResponseEntity<ApiRes<UserDetailRes>> approveUser(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @RequestBody ApproveReq req
    ) {

        ApproveCommand command = new ApproveCommand(
            req.userId(),
            req.status()
        );

        UserResult userResult = userService.approve(command);
        return ResponseEntity.ok(
            ApiRes.success(UserDetailRes.from(userResult))
        );
    }

    @GetMapping("/{userId}")
    @PassportAuthorize(checkResult = true)
    @Operation(summary = "유저 단건 조회", description = "userId로 특정 유저를 조회합니다.")
    public ResponseEntity<ApiRes<UserDetailRes>> getUser(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                UserDetailRes.from(
                    userService.getUser(userId)
                )
            )
        );
    }

    @GetMapping("/name/{name}")
    @PassportAuthorize(checkResult = true)
    @Operation(summary = "유저 이름으로 조회", description = "이름 기준으로 유저 정보를 조회합니다.")
    public ResponseEntity<ApiRes<UserDetailRes>> getUserByName(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PathVariable String name
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                UserDetailRes.from(
                    userService.getUserByName(name)
                )
            )
        );
    }

    @GetMapping
    @PassportAuthorize
    @Operation(summary = "유저 목록 조회", description = "전체 유저 목록을 페이지네이션하여 조회합니다.")
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsers(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                userService.getUsers(pageable)
                    .map(UserDetailRes::from)
            )
        );
    }

    @GetMapping("/status/{req}")
    @PassportAuthorize
    @Operation(summary = "유저 상태 기준 조회", description = "상태(UserStatus)에 따라 유저 목록을 조회합니다.")
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsersByStatus(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
        @PathVariable String req
    ) {
        UserStatus status = UserStatus.of(req);
        return ResponseEntity.ok(
            ApiRes.success(
                userService.getUsersByStatus(pageable, status)
                    .map(UserDetailRes::from)
            )
        );
    }

    @GetMapping("/role/{req}")
    @PassportAuthorize
    @Operation(summary = "유저 역할 기준 조회", description = "역할(UserRole)에 따라 유저 목록을 조회합니다.")
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsersByRole(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createdAt") Pageable pageable,
        @PathVariable String req
    ) {
        UserRole role = UserRole.of(req);
        return ResponseEntity.ok(
            ApiRes.success(
                userService.getUsersByRole(pageable, role)
                    .map(UserDetailRes::from)
            )
        );
    }

    @DeleteMapping("/{userId}")
    @PassportAuthorize(checkResult = true)
    @Operation(summary = "유저 삭제", description = "userId 기준으로 유저를 삭제합니다.")
    public ResponseEntity<ApiRes<UserDetailRes>> deleteUser(
        @Parameter(hidden = true) @PassportUser Passport passport,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(
                UserDetailRes.from(
                    userService.deleteUser(userId, passport.getUserId())
                )
            )
        );
    }
}
