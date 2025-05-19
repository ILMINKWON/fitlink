package com.sp.fitlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.fitlink.mapper.FitLinkMapper;

@Service
public class FitLinkService {

    @Autowired
    private FitLinkMapper fitLinkMapper;

    public boolean checkAdminCode(String inputCode){
        String savedCode = fitLinkMapper.findByAdminCode(); // DB에서 저장된 인증코드 조회
        return inputCode.equals(savedCode); // 비교
    }

}
