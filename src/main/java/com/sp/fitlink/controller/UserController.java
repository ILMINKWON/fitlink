package com.sp.fitlink.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.sp.fitlink.dto.AdminDto;
import com.sp.fitlink.dto.KakaoLoginDto;
import com.sp.fitlink.service.FitLinkService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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

    @PostMapping("/kakao-login")
    public ResponseEntity<String> kakaoLogin(@RequestBody KakaoLoginDto dto , Session session) {
        // dto에서 kakaoId, nickname 받아서 로그인 처리
        // 성공 시 ResponseEntity.ok("success")
        // 실패 시 ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("fail") 등

        return ResponseEntity.ok("success");
    }

     //---------------------------------------------------------------------------------

     @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 종료
        return "redirect:/fitLink/mainPage";
    }

       //---------------------------------------------------------------------------------

    @RequestMapping("/admin-register")
    public String adminregister(){

        return "/admin/admin-register";
        
    }

      //---------------------------------------------------------------------------------

      @RequestMapping("/adminRegisterComplete")
      public String adminRegisterComplete(AdminDto adminDto){
  
        fitLinkService.adminRegisterComplete(adminDto);

          return "/admin/adminCompletePage";
          
      }

      //---------------------------------------------------------------------------------

      @RequestMapping("/ilminPage")
      public String ilminPage(){
  
          return "/admin/ilminPage";
          
      }

      //전화번호 중복체크

      @PostMapping("/checkPhone")
      public ResponseEntity<Boolean> checkPhone(@RequestParam(name="phone", required=true) String phone) {
          boolean exists = fitLinkService.existsByPhone(phone);
          return ResponseEntity.ok(exists); 
      }

      
}
