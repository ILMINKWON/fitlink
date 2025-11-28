package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class UserNotificationDto {
    private int id;
    private Long kakaoUserId;
    private String message;
    private boolean isRead;
}
