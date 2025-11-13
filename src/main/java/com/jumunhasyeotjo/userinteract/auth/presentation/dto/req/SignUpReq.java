package com.jumunhasyeotjo.userinteract.auth.presentation.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SignUpReq(
    @NotBlank(message = "이름을 입력해주세요.")
    String name,
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상, 15자 이하입니다.")
    @Pattern(
        regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$",
        message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자(@$!%*?&)를 포함해야 합니다."
    )
    String password,
    @NotBlank(message = "슬랙 아이디를 입력해주세요.")
    @Email
    String slackId,
    @NotBlank(message = "역할을 입력해주세요")
    Role role,
    UUID belong
) {

}
