package com.sp.fitlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.sp.fitlink.dto.KakaoLoginDto;
import com.sp.fitlink.service.FitLinkService;

@RestController
public class KakaoLoginController {

    @Autowired
    private FitLinkService fitLinkService;

    @GetMapping("/fitLink/kakao-login")
    public String kakaoLogin(KakaoLoginDto dto) {
        if (fitLinkService.existsByKakaoId(dto.getKakaoId())) {
            return "success";
        }else{
            throw new RuntimeException("인증 실패");
        }
    }
}
