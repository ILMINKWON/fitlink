package com.sp.fitlink.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.fitlink.service.FitLinkService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("fitLink")
public class UserController {

    @Autowired
    private FitLinkService fitLinkService;

    // 관리자용 코드 트랜잭션 처리
   @PostMapping("/verifyAdminCode")
    @Transactional
    public void verifyAdminCode(@RequestParam("inputCode") String inputCode, HttpServletResponse response) throws IOException {
        boolean isVerified = fitLinkService.checkAdminCode(inputCode);

        if (isVerified) {
            response.getWriter().write("success");
        } else {
            // 트랜잭션 롤백 유도
            throw new RuntimeException("인증 실패: 코드 불일치");
        }
    }

    //---------------------------------------------------------------------------------

    @RequestMapping("/mainPage")
    public String mainPage(){

        return "/user/mainPage";
        
    }
    
      //---------------------------------------------------------------------------------
}
