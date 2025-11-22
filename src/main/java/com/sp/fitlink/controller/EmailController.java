package com.sp.fitlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.sp.fitlink.service.EmailService;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-code")
    public String sendCode(@RequestParam("email") String email, @RequestParam("code") String code) {
        emailService.sendCode(email, code);
        return "이메일 전송 완료";
    }
}
