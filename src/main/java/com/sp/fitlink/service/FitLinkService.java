package com.sp.fitlink.service;

import com.sp.fitlink.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sp.fitlink.controller.NotificationWebSocketController;
import com.sp.fitlink.mapper.FitLinkMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; 

import java.util.stream.Collectors;


@Service
public class FitLinkService {

    @Autowired
    private FitLinkMapper fitLinkMapper;

    @Autowired
    private NotificationWebSocketController socketController;

    public AdminDto checkAdminCode(String inputCode){
        return fitLinkMapper.findByAdminCode(inputCode);
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


     //-------------------------------------------------------------------------------------

     public List<Map<String, String>> searchByName(String keyword) {
        List<AdminDto> matched = fitLinkMapper.findByNameContaining(keyword);
        return matched.stream()
            .map(l -> Map.of("name", l.getName(), "phone", l.getPhone(), "email", l.getEmail()))
            .collect(Collectors.toList());
    }

    public AdminDto findAdminById(int id){
        return fitLinkMapper.findAdminId(id);
    }

    public void saveReservation(ReservationRequestDto reservationRequestDto){
        LocalDateTime checkInTime = LocalDateTime.parse(reservationRequestDto.getCheckIn());
        LocalDateTime checkOutTime = LocalDateTime.parse(reservationRequestDto.getCheckOut());

        // LocalDateTime -> String (DB DATETIME 형식에 맞춤)
        String checkInStr = checkInTime.toString().replace("T", " "); // 2025-11-27 11:00:00
        String checkOutStr = checkOutTime.toString().replace("T", " "); // 2025-11-27 12:00:00

        // check_out_time 1시간 증가
//        LocalDateTime checkOutTime = LocalDateTime.parse(checkOut.replace(" ", "T"))
//                .plusHours(1);
//        checkOut = checkOutTime.toString().replace("T", " ");


        fitLinkMapper.insertReservation(
                reservationRequestDto.getAdminId(), reservationRequestDto.getUserName(), reservationRequestDto.getUserPhone(),
                checkInStr, checkOutStr);

    }

    public List<String> findReservedTimes(int adminId, LocalDate date){
        return fitLinkMapper.findReservedTimes(adminId, date);
    }

    public boolean isAlreadyReserved(int adminId, LocalDateTime checkIn) {
        return fitLinkMapper.countReserved(adminId, checkIn) > 0;
    }

    public List<ReservationDto> findReservationsByAdminId(int adminId) {
        return fitLinkMapper.findByAdminId(adminId);
    }

}
