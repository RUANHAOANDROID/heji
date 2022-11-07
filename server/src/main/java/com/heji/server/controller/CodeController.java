package com.heji.server.controller;

import com.heji.server.model.base.ApiResponse;
import com.heji.server.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/code")
@Slf4j
public class CodeController {
    final
    CodeService verificationService;

    public CodeController(CodeService userService) {
        this.verificationService = userService;
    }

    @ResponseBody
    @PostMapping(value = {"/getRandom"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getCode() {
        String code =verificationService.createCode();
        return ApiResponse.success(code);
    }

    @ResponseBody
    @PostMapping(value = {"/deleteCode"}, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String deleteCode(@RequestParam String  code) {
        verificationService.deleteCode(code);
        return ApiResponse.success("OK");
    }


}
