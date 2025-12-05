package com.sp.fitlink.controller;

import com.sp.fitlink.dto.*;
import com.sp.fitlink.service.FitLinkService;
import com.sp.fitlink.service.RestService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.web.bind.annotation.RestController

@RequestMapping("/fitLink/api")
@RequiredArgsConstructor
public class RestController {

    private final RestService restService;
    private final FitLinkService fitLinkService;

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

    //ResponseEntity<String> = HTTP 응답 데이터와 상태코드를 담아서 반환하겠다 → 응답 Body는 String 형태
    //ResponseEntity.ok("ok") = HTTP 200(성공) + 문자열 "ok" 응답 전송
    //GET은 조회용
    //POST는 등록/수정/삭제(상태변화)용
    @PostMapping("/reservation")
    public ResponseEntity<String> saveReservation(
            @RequestBody ReservationRequestDto reservationRequestDto,
            HttpSession session) {

        Long kakaoUserId = (Long) session.getAttribute("kakaoUserId");
        if (kakaoUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인이 필요합니다.");
        }

        reservationRequestDto.setKakaoUserId(kakaoUserId);

        LocalDateTime checkInDateTime =
                LocalDateTime.parse(reservationRequestDto.getCheckIn());

        if (fitLinkService.isAlreadyReserved(reservationRequestDto.getAdminId(), checkInDateTime)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 예약된 시간입니다.");
        }

        fitLinkService.saveReservation(reservationRequestDto);

        String formattedCheckIn = reservationRequestDto.getCheckIn().replace("T", " ");
        String message = formattedCheckIn.substring(0, 16) + " " +
                reservationRequestDto.getAdminName() +
                " 트레이너와의 수업이 예약되었습니다! 💪";

        fitLinkService.createReservationNotification(kakaoUserId, message);

        // 🔥 여기서 ready 호출 + 세션에 tid 저장
        KaKaoPayResponse payRes = fitLinkService.kakaoPayReady(reservationRequestDto, session);

        // 프론트에는 redirect URL만 내려줌
        return ResponseEntity.ok(payRes.getNext_redirect_pc_url());
    }


    @GetMapping("/reservation/times")
    public ResponseEntity<List<String>> getReservationTimes(@RequestParam("adminId") int adminId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        List<String> times = fitLinkService.findReservedTimes(adminId, date);
        return ResponseEntity.ok(times);
    }

//    @PostMapping("/reservation")
//    public ResponseEntity<String> saveReservation(@RequestBody ReservationRequestDto dto) {
//
//
//
//        LocalDateTime checkInDateTime = LocalDateTime.parse(dto.getCheckIn());
//
//        if (fitLinkService.isAlreadyReserved(dto.getAdminId(), checkInDateTime)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                    .body("이미 예약된 시간입니다.");
//        }
//
//        fitLinkService.saveReservation(dto);
//
//        return ResponseEntity.ok("예약 완료");
//    }

    @GetMapping("/reservation/admin")
    public ResponseEntity<List<ReservationDto>> getAdminReservations(@RequestParam("adminId") int adminId) {

        List<ReservationDto> reservations = fitLinkService.findReservationsByAdminId(adminId);

        return  ResponseEntity.ok(reservations);
    }

    @GetMapping("/notifications")
    public List<UserNotificationDto> getUserNotifications(HttpSession session){
        Long kakaoUserId = (Long) session.getAttribute("kakaoUserId");
        if (kakaoUserId == null) {
            return List.of();
        }
        return fitLinkService.getUserNotification(kakaoUserId);
        }

    @GetMapping("/pay/success")
    public String kakaoPaySuccess(@RequestParam("pg_token") String pgToken,
                                  HttpSession session) {

        String tid = (String) session.getAttribute("tid");

        if (tid != null) {
            fitLinkService.kakaoPayApprove(tid, pgToken, session);
            session.removeAttribute("tid");
            session.removeAttribute("partner_user_id");
        }

        return "redirect:/fitLink/fitLinkUser?paySuccess=true";
    }

    @DeleteMapping("/reservation/cancel/{id}")
    public ResponseEntity<String> cancelReservation(@PathVariable int id){
        ReservationDto reservationDto = fitLinkService.findReservationById(id);

        if(reservationDto == null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("예약 없음");
        }

        fitLinkService.reservationCancel(id);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = reservationDto.getCheckIn().format(formatter);

        // 알림 메시지
        String message = String.format(
                "%s 트레이너와의 %s 수업 예약이 취소되었습니다. ❌",
                reservationDto.getAdminName(),
                formattedDateTime
        );

        // 🔥 카카오 유저일 때만 알림 저장
        if(reservationDto.getKakaoUserId() != null) {
            fitLinkService.createReservationNotification(reservationDto.getKakaoUserId(), message);
        }

        return ResponseEntity.ok("취소완료");
    }

    @GetMapping("/dashboard/data")
    public ResponseEntity<DashboardResponseDto> dashboard(){
        DashboardResponseDto dto = new DashboardResponseDto();

        dto.setTrainerCount(fitLinkService.trainerByCount());
        dto.setMemberCount(fitLinkService.customerByCount());
        dto.setTopRank(fitLinkService.trainerByTop());
        dto.setReservationToday(fitLinkService.countToday());
//        dto.setNewNotifications(fitLinkService.countUnread());
//
//        DashboardResponseDto.WeeklyReservationDto chartDto =
//                new DashboardResponseDto.WeeklyReservationDto();
//        chartDto.setLabels(List.of("월","화","수","목","금","토","일"));
//        chartDto.setData(reservationService.getWeeklyStats());
//        dto.setWeeklyReservation(chartDto);
//
//        dto.setRecentTrainers(trainerService.getRecent(5));

        return ResponseEntity.ok(dto);
    }

    @GetMapping("/customer")
    @ResponseBody
    public Map<String,Object> customerListAPI(@RequestParam(defaultValue = "1") int page){
        int pageSize = 10;
        int total = fitLinkService.customerByCount();
        int offset = (page-1) * pageSize;

        List<UserDto> list = fitLinkService.findCustomers(offset, pageSize);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        Map<String, Object> map = new HashMap<>();
        map.put("totalPages", totalPages);
        map.put("currentPage", page);
        map.put("list", list);

        return map;
    }

    @GetMapping("/rank")
    @ResponseBody
    public Map<String,Object> rank(@RequestParam(defaultValue = "1") int page){
        int pageSize = 5;
        int offset = (page-1) * pageSize;

        List<AdminRankDto> list = fitLinkService.findRankOfTrainers(offset,pageSize);
        Map<String,Object> map = new HashMap<>();
        map.put("totalPages", list.size());
        map.put("currentPage", page);
        map.put("list", list);

        return map;
    }

    @GetMapping("/dashboard/chart")
    public DashboardChartDto getChart() {
        return fitLinkService.getReservationChartData();
    }

    @GetMapping("/recent-trainers")
    public List<AdminDto> recentTrainers() {
        return fitLinkService.getRecentTrainers();
    }

}
