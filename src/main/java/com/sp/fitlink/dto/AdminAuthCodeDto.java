package com.sp.fitlink.dto;

import org.springframework.boot.autoconfigure.domain.EntityScan;

import lombok.Data;

@Data
public class AdminAuthCodeDto {
    private long id;
    private String code;
}
