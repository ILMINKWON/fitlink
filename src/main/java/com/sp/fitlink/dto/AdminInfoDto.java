package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class AdminInfoDto {
    private Long id;
    private String name;
    private String gender;
    private String specialty;
    private String workplaceName;
    private String address;
    private Double lat;
    private Double lng;
    private int reviewCount;
}
