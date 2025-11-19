package com.sp.fitlink.controller;

import com.sp.fitlink.dto.AdminDto;
import com.sp.fitlink.dto.AdminInfoDto;
import com.sp.fitlink.dto.ReviewDto;
import com.sp.fitlink.service.RestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/fitLink/api")
@RequiredArgsConstructor
public class RestController {

    private final RestService restService;

    @GetMapping("/admin/search")
    public List<AdminInfoDto> search(
            @RequestParam(required = false) Integer workplaceId,  // workplace_name 테이블의 id
            @RequestParam(required = false) String gender,          // 'M' or 'F'
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) Integer reviewCount
    ) {
        return restService.search(workplaceId, gender, specialty, reviewCount);
    }

    @GetMapping("/review/list")
    @ResponseBody
    public List<ReviewDto> getReviews(@RequestParam("adminId") int adminId) {
        return restService.findReviewsByAdmin(adminId);
    }
}