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
    private String favorite;
    private String workplace;
    private String certificateFileName;
    private String code;
    private int workplaceid;
}
