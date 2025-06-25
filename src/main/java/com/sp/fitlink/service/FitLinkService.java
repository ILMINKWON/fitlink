package com.sp.fitlink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.fitlink.controller.NotificationWebSocketController;
import com.sp.fitlink.dto.AdminDto;
import com.sp.fitlink.dto.ManagerDto;
import com.sp.fitlink.dto.NotificationDto;
import com.sp.fitlink.mapper.FitLinkMapper;

@Service
public class FitLinkService {

    @Autowired
    private FitLinkMapper fitLinkMapper;

    @Autowired
    private NotificationWebSocketController socketController;

    public boolean checkAdminCode(String inputCode){
        String savedCode = fitLinkMapper.findByAdminCode(); // DB에서 저장된 인증코드 조회
        return inputCode.equals(savedCode); // 비교
    }

    //-------------------------------------------------------------------------------------

    public boolean existsByKakaoId(Long kakaoId) {
        return fitLinkMapper.countByKakaoId(kakaoId) > 0;
    }

     //-------------------------------------------------------------------------------------

     public void adminRegisterComplete(AdminDto adminDto){

        fitLinkMapper.adminRegisterComplete(adminDto);

        ManagerDto managerDto = fitLinkMapper.findManagerById(1);

         // 알림 객체 생성
         NotificationDto notificationDto = new NotificationDto();
         notificationDto.setSender_id(adminDto.getId());
         notificationDto.setRecipient_id(managerDto.getId());
         notificationDto.setMessage("한명의 지도자가 회원가입을 하였습니다. 클릭하여 코드번호를 전송해주세요.");
         notificationDto.set_read(false);

         fitLinkMapper.insertNotification(notificationDto);

             // WebSocket으로 관리자 ID 1에게 전송
        socketController.sendNewLeaderNotification(1L, notificationDto.getMessage());

     }

       //전화번호 중복체크

     public boolean existsByPhone(String phone) {
        
        return fitLinkMapper.countByPhone(phone) > 0;
    }

    



    
}
