package com.sp.fitlink.controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsProperties.Listener.Session;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/fitLink")
public class UserController {

    @Autowired
    private FitLinkService fitLinkService;

    // 관리자용 코드 트랜잭션 처리
   @PostMapping("/verifyAdminCode")
    @Transactional
    public  ResponseEntity<String> verifyAdminCode(@RequestParam("inputCode") String inputCode, HttpSession session) throws IOException {

        AdminDto isVerified = fitLinkService.checkAdminCode(inputCode);

        if (isVerified != null) {
            session.setAttribute("loginAdmin" , isVerified.getId());
            return ResponseEntity.ok("success");
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
      public String adminRegisterComplete(AdminDto adminDto,  @RequestParam("certificate_path") MultipartFile certificateFile) throws IOException {

              // 저장 경로 설정
              String uploadDir = "C:/upload/certificates/";
              File dir = new File(uploadDir);
              if (!dir.exists()) dir.mkdirs();

              // 파일명 생성
              String fileName = System.currentTimeMillis() + "_" + certificateFile.getOriginalFilename();
              File destination = new File(uploadDir + fileName);

              // 파일 저장
              certificateFile.transferTo(destination);

              // DTO에 저장된 파일 경로 설정
              adminDto.setCertificateFileName(fileName);

              // DB 저장
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

        @GetMapping("/fitLinkUser")
        public String fitLinkUserPage() {
            return "user/fitLinkUser";   // templates/fitLinkUser.html
        }

        //채팅 컨트롤러
        @GetMapping("/chat")
        public String chatPage(@RequestParam int adminId, @RequestParam String name, Model model) {

            model.addAttribute("adminId", adminId);
            model.addAttribute("name", name);

            return "/user/chatPage";
        }

    @RequestMapping("/adminPage")
    public String adminPage(HttpSession session, Model model) {

        try {
            Object adminIdObj = session.getAttribute("loginAdmin");

            // 세션에 관리자 정보가 없는 경우 → 로그인 페이지로 리다이렉트
            if (adminIdObj == null) {
                return "redirect:/";
            }

            int adminId = (int) adminIdObj;

            AdminDto admin = fitLinkService.findAdminById(adminId);

            // 관리자 정보가 DB에 없는 경우 → 오류 페이지 or 홈으로
            if (admin == null) {
                return "redirect:/";
            }

            model.addAttribute("admin", admin);

            return "admin/adminPage";

        } catch (Exception e) {
            e.printStackTrace(); // 서버 로그에 기록

            return "redirect:/";  // 문제가 생기면 홈으로 이동
        }
    }

}
