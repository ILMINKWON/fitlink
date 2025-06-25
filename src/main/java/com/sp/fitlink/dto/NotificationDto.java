package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class NotificationDto {
    private int id;
    private int sender_id;
    private int recipient_id;
    private String message;
    private boolean is_read = false;
}
