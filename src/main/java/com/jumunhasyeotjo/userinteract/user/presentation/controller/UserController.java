package com.jumunhasyeotjo.userinteract.user.presentation.controller;

import com.jumunhasyeotjo.userinteract.common.ApiRes;
import com.jumunhasyeotjo.userinteract.common.annotation.PassportAuthorize;
import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.application.command.ApproveCommand;
import com.jumunhasyeotjo.userinteract.user.application.dto.UserResult;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.ApproveReq;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.res.*;
import com.library.passport.annotation.PassportUser;
import com.library.passport.proto.PassportProto.Passport;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PatchMapping("/approve")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<UserDetailRes>> approveUser(
        @PassportUser Passport passport,
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
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER},
        checkResult = true
    )
    public ResponseEntity<ApiRes<UserDetailRes>> getUser(
        @PassportUser Passport passport,
        @PathVariable Long userId
    ) {
        UserResult userResult = userService.getUser(userId);
        return ResponseEntity.ok(
            ApiRes.success(UserDetailRes.from(userResult))
        );
    }

    @GetMapping("/name/{name}")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER},
        checkResult = true
    )
    public ResponseEntity<ApiRes<UserDetailRes>> getUserByName(
        @PassportUser Passport passport,
        @PathVariable String name
    ) {
        UserResult userResult = userService.getUserByName(name);

        return ResponseEntity.ok(
            ApiRes.success(UserDetailRes.from(userResult))
        );
    }

    @GetMapping("/")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsers(
        @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable
    ) {
        return ResponseEntity.ok(
            ApiRes.success(userService.getUsers(pageable).map(UserDetailRes::from))
        );
    }

    @GetMapping("/status/{req}")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsersByStatus(
        @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable,
        @PathVariable String req
    ) {
        UserStatus status = UserStatus.of(req);
        return ResponseEntity.ok(
            ApiRes.success(userService.getUsersByStatus(pageable, status).map(UserDetailRes::from))
        );
    }

    @GetMapping("/role/{req}")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER}
    )
    public ResponseEntity<ApiRes<Page<UserDetailRes>>> getUsersByRole(
        @PassportUser Passport passport,
        @PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable,
        @RequestParam String req
    ) {
        UserRole role = UserRole.of(req);
        return ResponseEntity.ok(
            ApiRes.success(userService.getUsersByRole(pageable, role).map(UserDetailRes::from))
        );
    }

    @DeleteMapping("/{userId}")
    @PassportAuthorize(
        allowedRoles = {UserRole.MASTER, UserRole.HUB_MANAGER},
        checkResult = true
    )
    public ResponseEntity<ApiRes<UserDetailRes>> deleteUser(
        @PassportUser Passport passport,
        @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
            ApiRes.success(UserDetailRes.from(userService.deleteUser(userId, passport.getUserId())))
        );
    }
}
