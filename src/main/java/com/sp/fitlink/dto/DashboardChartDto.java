package com.sp.fitlink.dto;

import lombok.Data;
import java.util.List;

//차트.js로 내려줄 응답 DTO
@Data
public class DashboardChartDto {
    private List<String> labels;  // ["12-01","12-02", ...]
    private List<Integer> values; // [3, 5, 0, 7, ...]
}
