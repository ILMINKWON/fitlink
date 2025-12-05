package com.sp.fitlink.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationStatDto {
    private LocalDate statDate;
    private int count;
}
