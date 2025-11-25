package com.jumunhasyeotjo.userinteract.auth.presentation;

import com.jumunhasyeotjo.userinteract.auth.application.PassportService;
import com.jumunhasyeotjo.userinteract.auth.presentation.dto.res.PassportRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/passports")
@RequiredArgsConstructor
public class PassportController {
    private final PassportService passportService;

    @PostMapping()
    public PassportRes issuePassport(@RequestParam("jwt") String jwt) {
        System.out.println(jwt);
        return PassportRes.fromPassportResult(passportService.issuePassport(jwt));
    }
}
