package com.jumunhasyeotjo.userinteract.user.presentation.controller;

import com.jumunhasyeotjo.userinteract.user.application.UserService;
import com.jumunhasyeotjo.userinteract.user.application.command.ApproveCommand;
import com.jumunhasyeotjo.userinteract.user.application.command.JoinCommand;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserRole;
import com.jumunhasyeotjo.userinteract.user.domain.vo.UserStatus;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.ApproveReq;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.req.JoinReq;
import com.jumunhasyeotjo.userinteract.user.presentation.dto.res.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/")
    public UserDetailRes join(@RequestBody JoinReq req) {
        JoinCommand command = new JoinCommand(
            req.name(),
            req.password(),
            req.slackId(),
            req.role(),
            req.belong()
        );

        return UserDetailRes.from(userService.join(command));
    }

    @PatchMapping("/approve")
    public UserDetailRes approveUser(@RequestBody ApproveReq req) {
        ApproveCommand command = new ApproveCommand(
            req.userId(),
            req.status()
        );
        return UserDetailRes.from(userService.approve(command));
    }

    @GetMapping("/{userId}")
    public UserDetailRes getUser(@PathVariable Long userId) {
        return UserDetailRes.from(userService.getUser(userId));
    }

    @GetMapping("/")
    public Page<UserDetailRes> getUsers(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable) {
        return userService.getUsers(pageable).map(UserDetailRes::from);
    }

    @GetMapping("/status/{req}")
    public Page<UserDetailRes> getUsersByStatus(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable, @PathVariable String req) {
        UserStatus status = UserStatus.of(req);
        return userService.getUsersByStatus(pageable, status).map(UserDetailRes::from);
    }

    @GetMapping("/role/{req}")
    public Page<UserDetailRes> getUsersByRole(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable, @RequestParam String req) {
        UserRole role = UserRole.of(req);
        return userService.getUsersByRole(pageable, role).map(UserDetailRes::from);
    }

    @GetMapping("/companyDriver/{hubId}")
    public Page<CompanyDriverDetailRes> getCompanyDriverByHubId(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable, @PathVariable UUID hubId) {
        return userService.getCompanyDriverByHubId(pageable, hubId).map(CompanyDriverDetailRes::from);
    }

    @GetMapping("/hubDriver")
    public Page<HubDriverDetailRes> getHubDriverByHubId(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable) {
        return userService.getHubDriverByHubId(pageable).map(HubDriverDetailRes::from);
    }

    @GetMapping("/hubManager/{hubId}")
    public Page<HubManagerDetailRes> getHubManagerByHubId(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable, @PathVariable UUID hubId) {
        return userService.getHubManagerByHubId(pageable, hubId).map(HubManagerDetailRes::from);
    }

    @GetMapping("/companyManager/{companyId}")
    public Page<CompanyManagerDetailRes> getCompanyManagerByCompanyId(@PageableDefault(page = 0, size = 10, sort = "createAt") Pageable pageable, @PathVariable UUID companyId) {
        return userService.getCompanyManagerByCompanyId(pageable, companyId).map(CompanyManagerDetailRes::from);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }


}
