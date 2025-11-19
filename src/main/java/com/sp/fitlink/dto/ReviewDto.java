package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class ReviewDto {
    private int id;
    private int adminId;
    private int rating;
    private String content;
    private String created_at;
}
