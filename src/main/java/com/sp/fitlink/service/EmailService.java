package com.sp.fitlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("지도자 코드 전송");
        message.setText("안녕하세요.\n\n fitLink에서 확인한 결과 인증 완료되었습니다. \n\n 인증코드는 다음과 같습니다:\n\n" + code + "\n\n 인증코드를 개인정보 수정에서 코드를 넣어주셔야 활동 가능합니다. \n\n 감사합니다.");
        mailSender.send(message);
    }
}
