package com.sp.fitlink.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.Data;

//웹 소켓 컨트롤러
@Controller
public class NotificationWebSocketController {
 @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendNewLeaderNotification(Long adminId, String message) {
        NotificationPayload payload = new NotificationPayload(message);
        messagingTemplate.convertAndSend("/topic/notifications/" + adminId, payload);
    }

    // DTO
    @Data
    @AllArgsConstructor
    public static class NotificationPayload {
        private String message;
    }
}
