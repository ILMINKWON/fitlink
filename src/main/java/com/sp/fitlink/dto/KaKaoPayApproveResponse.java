package com.sp.fitlink.dto;

import lombok.Data;

@Data
public class KaKaoPayApproveResponse {
    private String aid;
    private String tid;
    private String cid;
    private String status;
    private Amount amount;

    @Data
    public static class Amount {
        private int total;
        private int tax_free;
        private int vat;
    }
}
