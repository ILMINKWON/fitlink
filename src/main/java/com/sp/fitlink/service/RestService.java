package com.sp.fitlink.service;

import com.sp.fitlink.dto.AdminDto;
import com.sp.fitlink.dto.AdminInfoDto;
import com.sp.fitlink.dto.ReviewDto;
import com.sp.fitlink.mapper.FitLinkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestService {

    private final FitLinkMapper fitLinkMapper;

    public List<AdminInfoDto> search(Integer workplaceId, String gender, String specialty, Integer reviewCount) {
        return fitLinkMapper.trainerInfo(workplaceId, gender, specialty, reviewCount);
    }

    public List<ReviewDto> findReviewsByAdmin(Integer adminId) {
        return fitLinkMapper.findReviewsAdmin(adminId);
    }
}