package com.sp.fitlink.dto;

import lombok.Data;

import java.util.List;

//중첩 DTO = > 확장 & 유지보수 쉬움
@Data
public class DashboardResponseDto {
    private int trainerCount;
    private int memberCount;
    private String topRank;
    private int reservationToday;
    private int newNotifications;

    private WeeklyReservationDto weeklyReservation;

    private List<RecentTrainerDto> recentTrainers;

    @Data
    public static class RecentTrainerDto {
        private String name;
        private String joinDate;
    }

    @Data
    public static class WeeklyReservationDto {
        private List<String> labels;
        private List<Integer> data;
        private String name;
        private String joinDate;
    }

}
