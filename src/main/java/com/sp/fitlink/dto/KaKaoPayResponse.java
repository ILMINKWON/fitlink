package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class KaKaoPayResponse {
    private String tid;                    // 결제 고유 번호
    private String next_redirect_app_url;  // 앱 결제 URL
    private String next_redirect_mobile_url; // 모바일 결제 URL
    private String next_redirect_pc_url;   // PC 결제 URL
    private String created_at;             // 생성 시간
}
