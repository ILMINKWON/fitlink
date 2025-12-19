package com.sp.fitlink.mapper;

import com.sp.fitlink.dto.*;
import org.apache.catalina.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map; 

@Mapper
public interface FitLinkMapper {

    public AdminDto findByAdminCode(String inputCode);
    
    public String findByKakaoCode();

    public int countByKakaoId(Long kakaoId); // ← 카카오 ID 존재 여부 확인용

    public void adminRegisterComplete(AdminDto adminDto);

    //전화번호 중복체크
    public int countByPhone(String phone);

    public int insertNotification(NotificationDto notificationDto);

    public ManagerDto findManagerById(int id);

    public List<AdminDto> findByNameContaining(@Param("keyword") String keyword);

    public List<AdminInfoDto> trainerInfo(@Param("workplaceId") Integer workplaceId, @Param("gender") String gender, @Param("specialty") String specialty, @Param("reviewCount") Integer reviewCount);

    public List<ReviewDto> findReviewsAdmin(@Param("adminId") Integer adminId);

    public AdminDto findAdminId(int id);

    public void insertReservation( @Param("adminId") int adminId,
                                   @Param("userName") String userName,
                                   @Param("userPhone") String userPhone,
                                   @Param("checkIn") String checkIn,
                                   @Param("checkOut") String checkOut,
                                   @Param("kakaoUserId") Long kakaoUserId);

    public List<String> findReservedTimes(@Param("adminId")int adminId, @Param("date")LocalDate date);

    // 예약 카운트
    public int countReserved(@Param("adminId")int adminId, @Param("checkIn")LocalDateTime checkIn);

    public List<ReservationDto> findByAdminId(int adminId);

    public boolean existsReservation(@Param("adminId") int adminId,
                              @Param("checkIn") LocalDateTime checkIn);

    public void insertUserNotification(UserNotificationDto dto);

    public List<UserNotificationDto> findByKakaoUserId(Long kakaoUserId);

    public void reservationRealCancel(@Param("id") int id);

    public ReservationDto findReservationById(@Param("id") int id);

    public int trainerCount();

    public int customerCount();

    public String topRank();

    public int countByToday();

    public List<AdminDto> findTrainersByPage(int offset, int pageSize);

    public List<UserDto> findMembers(int offset, int pageSize);

    public List<AdminRankDto> findTrainerRank(int offset, int pageSize);

    public List<ReservationStatDto> findReservationStatsLast7Days();

    public List<AdminDto> findRecentTrainers();

    public void adminCodeRegisterComplete(int adminId, String adminCode);
}
