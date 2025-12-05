package com.sp.fitlink.service;

import com.sp.fitlink.dto.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.sp.fitlink.controller.NotificationWebSocketController;
import com.sp.fitlink.mapper.FitLinkMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
                checkInStr, checkOutStr,reservationRequestDto.getKakaoUserId());

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

    public void createReservationNotification(Long kakaoUserId,
                                              String message) {

        UserNotificationDto dto = new UserNotificationDto();
        dto.setKakaoUserId(kakaoUserId);
        dto.setMessage(message);

        fitLinkMapper.insertUserNotification(dto);
    }

    public List<UserNotificationDto> getUserNotification(Long kakaoUserId) {
        return fitLinkMapper.findByKakaoUserId(kakaoUserId);
    }

    public KaKaoPayResponse kakaoPayReady(ReservationRequestDto dto, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK f1749816af1b06176ff99b20f3a759e2");
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("partner_order_id", "1001");
        params.add("partner_user_id", dto.getKakaoUserId().toString());
        params.add("item_name", dto.getAdminName() + " PT 수업");
        params.add("quantity", "1");
        params.add("total_amount", "1000");
        params.add("tax_free_amount", "0");
        params.add("approval_url", "http://localhost:8888/fitLink/pay/success");
        params.add("cancel_url",   "http://localhost:8888/fitLink/pay/cancel");
        params.add("fail_url",     "http://localhost:8888/fitLink/pay/fail");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KaKaoPayResponse response = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                request,
                KaKaoPayResponse.class
        );

        // 🔥 승인 시 다시 쓰기 위해 세션 저장
        session.setAttribute("tid", response.getTid());
        session.setAttribute("partner_user_id", dto.getKakaoUserId().toString());

        return response;
    }

    public void kakaoPayApprove(String tid, String pgToken, HttpSession session) {
        RestTemplate restTemplate = new RestTemplate();

        String partnerUserId = (String) session.getAttribute("partner_user_id");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK f1749816af1b06176ff99b20f3a759e2");
        headers.set("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cid", "TC0ONETIME");
        params.add("tid", tid);
        params.add("partner_order_id", "1001");
        params.add("partner_user_id", partnerUserId); // 🔥 정확한 사용자 ID
        params.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        KaKaoPayApproveResponse approveRes = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                request,
                KaKaoPayApproveResponse.class
        );

        System.out.println("🎉 결제 승인 완료: " + approveRes);

        // 보안상 세션 정리
        session.removeAttribute("tid");
        session.removeAttribute("partner_user_id");
    }

    public void reservationCancel(int id){
        fitLinkMapper.reservationRealCancel(id);
    }

    public ReservationDto findReservationById(int id) {
        return fitLinkMapper.findReservationById(id);
    }

    public int trainerByCount(){
        return fitLinkMapper.trainerCount();
    }

    public int customerByCount(){
        return fitLinkMapper.customerCount();
    }

    public String trainerByTop(){
        return fitLinkMapper.topRank();
    }

    public int countToday(){
        return fitLinkMapper.countByToday();
    }

    public List<UserDto> findCustomers(int offset, int pageSize) {
        return fitLinkMapper.findMembers(offset,pageSize);
    }

    public List<AdminDto> findTrainers(int offset, int pageSize) {
        return fitLinkMapper.findTrainersByPage(offset,pageSize);
    }

    public List<AdminRankDto> findRankOfTrainers(int offset, int pageSize) {
        return fitLinkMapper.findTrainerRank(offset,pageSize);
    }

    public DashboardChartDto getReservationChartData() {
        List<ReservationStatDto> stats = fitLinkMapper.findReservationStatsLast7Days();

        List<String> labels = new ArrayList<>();
        List<Integer> values = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (ReservationStatDto s : stats) {
            labels.add(s.getStatDate().format(formatter));
            values.add(s.getCount());
        }

        DashboardChartDto dto = new DashboardChartDto();
        dto.setLabels(labels);
        dto.setValues(values);

        return dto;
    }

    public List<AdminDto> getRecentTrainers() {
        return fitLinkMapper.findRecentTrainers();
    }

}
