package com.eardream.global.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

/**
 * 날짜 관련 유틸리티 클래스
 */
public class DateUtils {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");
    
    private DateUtils() {
        // 인스턴스 생성 방지
    }
    
    /**
     * 현재 날짜 문자열 반환
     */
    public static String getCurrentDateString() {
        return LocalDate.now().format(DATE_FORMATTER);
    }
    
    /**
     * 현재 날짜시간 문자열 반환
     */
    public static String getCurrentDateTimeString() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }
    
    /**
     * 현재 년월 문자열 반환 (YYYY-MM)
     */
    public static String getCurrentMonthString() {
        return YearMonth.now().format(MONTH_FORMATTER);
    }
    
    /**
     * 특정 월의 둘째 주 일요일 반환
     */
    public static LocalDate getSecondSundayOfMonth(YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate firstSunday = firstDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return firstSunday.plusWeeks(1);
    }
    
    /**
     * 특정 월의 넷째 주 일요일 반환
     */
    public static LocalDate getFourthSundayOfMonth(YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate firstSunday = firstDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        return firstSunday.plusWeeks(3);
    }
    
    /**
     * 다음 정기 마감일 계산
     * @param monthlyDeadline 2(둘째주) 또는 4(넷째주)
     */
    public static LocalDate getNextDeadline(int monthlyDeadline) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate deadline;
        
        if (monthlyDeadline == 2) {
            deadline = getSecondSundayOfMonth(currentMonth);
        } else if (monthlyDeadline == 4) {
            deadline = getFourthSundayOfMonth(currentMonth);
        } else {
            throw new IllegalArgumentException("Invalid monthly deadline: " + monthlyDeadline);
        }
        
        // 이미 지난 경우 다음 달 마감일 반환
        if (deadline.isBefore(LocalDate.now()) || deadline.isEqual(LocalDate.now())) {
            YearMonth nextMonth = currentMonth.plusMonths(1);
            if (monthlyDeadline == 2) {
                deadline = getSecondSundayOfMonth(nextMonth);
            } else {
                deadline = getFourthSundayOfMonth(nextMonth);
            }
        }
        
        return deadline;
    }
    
    /**
     * 날짜 문자열을 LocalDate로 변환
     */
    public static LocalDate parseDate(String dateString) {
        return LocalDate.parse(dateString, DATE_FORMATTER);
    }
    
    /**
     * 날짜시간 문자열을 LocalDateTime으로 변환
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMATTER);
    }
    
    /**
     * LocalDate를 문자열로 변환
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * LocalDateTime을 문자열로 변환
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }
}