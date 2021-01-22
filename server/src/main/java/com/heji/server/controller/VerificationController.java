package com.heji.server.controller;

import com.heji.server.result.Result;
import com.heji.server.service.VerificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/verification")
@Slf4j
public class VerificationController {
    final
    VerificationService verificationService;

    public VerificationController(VerificationService userService) {
        this.verificationService = userService;
    }

    @ResponseBody
    @PostMapping(value = {"/getCode"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getCode() {
        String code =verificationService.createCode();
        return Result.success(code);
    }

    @ResponseBody
    @PostMapping(value = {"/deleteCode"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteCode(@RequestParam String  code) {
        verificationService.deleteCode(code);
        return Result.success("OK");
    }


}
