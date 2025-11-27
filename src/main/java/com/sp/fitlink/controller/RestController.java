package com.sp.fitlink.controller;

import com.sp.fitlink.dto.*;
import com.sp.fitlink.service.FitLinkService;
import com.sp.fitlink.service.RestService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
    public ResponseEntity<String> saveReservation(@RequestBody ReservationRequestDto reservationRequestDto) {
        LocalDateTime checkInDateTime = LocalDateTime.parse(reservationRequestDto.getCheckIn());

        if (fitLinkService.isAlreadyReserved(reservationRequestDto.getAdminId(), checkInDateTime)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 예약된 시간입니다.");
        }


        fitLinkService.saveReservation(reservationRequestDto);
        return ResponseEntity.ok("success");
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
}