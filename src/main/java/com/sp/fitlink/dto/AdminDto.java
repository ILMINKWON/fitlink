package com.sp.fitlink.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class AdminDto {
    private int id;
    private String name;
    private String gender;
    private LocalDate birth;
    private String email;
    private String phone;
    private String workplace;
    private String certificate_path;
    private String code;
}
