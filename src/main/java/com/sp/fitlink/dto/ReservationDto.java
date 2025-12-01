package com.sp.fitlink.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ReservationDto {
    private int id;
    private int adminId;
    private Long kakaoUserId;
    private String adminName;
    private int userId;
    private String status;
    private String userName;
    private String userPhone;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkIn;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime checkOut;
}
