package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class ReservationRequestDto {
    private int adminId;
    private String userName;
    private String userPhone;
    private String checkIn;   // "2025-11-25"
    private String checkOut;   // "09:00"
}
