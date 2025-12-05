package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class AdminInfoDto {
    private Long id;
    private String name;
    private String gender;
    private String specialty;
    private String workplaceName;
    private String workplace;
    private String address;
    private String certificateFileName;
    private Double lat;
    private Double lng;
    private int reviewCount;
}
