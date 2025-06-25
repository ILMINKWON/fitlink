package com.sp.fitlink.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.sp.fitlink.dto.AdminDto;
import com.sp.fitlink.dto.ManagerDto;
import com.sp.fitlink.dto.NotificationDto;

import java.util.List;
import java.util.Map; 

@Mapper
public interface FitLinkMapper {

    public String findByAdminCode();
    
    public String findByKakaoCode();

    public int countByKakaoId(Long kakaoId); // ← 카카오 ID 존재 여부 확인용

    public void adminRegisterComplete(AdminDto adminDto);

    //전화번호 중복체크
    public int countByPhone(String phone);

    public int insertNotification(NotificationDto notificationDto);

    public ManagerDto findManagerById(int id);

    public List<AdminDto> findByNameContaining(@Param("keyword") String keyword);


}
