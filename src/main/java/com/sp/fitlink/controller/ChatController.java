package com.sp.fitlink.controller;

import com.sp.fitlink.dto.ChatMessegeDto;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/send")
    @SendTo("/topic/chat")
    // @Payload = 메시지 본문(Body)을 자바 객체로 매핑해서 받기
    public ChatMessegeDto send(@Payload ChatMessegeDto chatMessegeDto) {
        return chatMessegeDto;
    }
}
