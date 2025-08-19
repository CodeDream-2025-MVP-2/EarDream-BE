package com.eardream.global.util;

import java.util.Random;
import java.util.UUID;

/**
 * 문자열 관련 유틸리티 클래스
 */
public class StringUtils {
    
    private static final String INVITE_CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int INVITE_CODE_LENGTH = 8;
    private static final Random RANDOM = new Random();
    
    private StringUtils() {
        // 인스턴스 생성 방지
    }
    
    /**
     * 빈 문자열 체크
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 빈 문자열이 아닌지 체크
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 초대 코드 생성 (8자리 대문자/숫자)
     */
    public static String generateInviteCode() {
        StringBuilder code = new StringBuilder(INVITE_CODE_LENGTH);
        for (int i = 0; i < INVITE_CODE_LENGTH; i++) {
            code.append(INVITE_CODE_CHARS.charAt(RANDOM.nextInt(INVITE_CODE_CHARS.length())));
        }
        return code.toString();
    }
    
    /**
     * UUID 생성 (하이픈 제거)
     */
    public static String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * 전화번호 포맷팅 (010-1234-5678)
     */
    public static String formatPhoneNumber(String phoneNumber) {
        if (isEmpty(phoneNumber)) {
            return phoneNumber;
        }
        
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        
        if (cleaned.length() == 11) {
            return String.format("%s-%s-%s", 
                cleaned.substring(0, 3),
                cleaned.substring(3, 7),
                cleaned.substring(7));
        } else if (cleaned.length() == 10) {
            return String.format("%s-%s-%s",
                cleaned.substring(0, 3),
                cleaned.substring(3, 6),
                cleaned.substring(6));
        }
        
        return phoneNumber;
    }
    
    /**
     * 텍스트 길이 제한 (말줄임표 추가)
     */
    public static String truncate(String text, int maxLength) {
        if (isEmpty(text) || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength - 3) + "...";
    }
    
    /**
     * 파일명에서 확장자 추출
     */
    public static String getFileExtension(String fileName) {
        if (isEmpty(fileName)) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
    
    /**
     * 안전한 파일명 생성 (특수문자 제거)
     */
    public static String sanitizeFileName(String fileName) {
        if (isEmpty(fileName)) {
            return fileName;
        }
        
        // 특수문자를 언더스코어로 대체
        String sanitized = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // 연속된 언더스코어를 하나로
        sanitized = sanitized.replaceAll("_+", "_");
        
        // 시작과 끝의 언더스코어 제거
        sanitized = sanitized.replaceAll("^_+|_+$", "");
        
        return sanitized;
    }
    
    /**
     * 마스킹 처리 (이름, 전화번호 등)
     */
    public static String mask(String text, int visibleStart, int visibleEnd) {
        if (isEmpty(text) || text.length() <= visibleStart + visibleEnd) {
            return text;
        }
        
        StringBuilder masked = new StringBuilder();
        masked.append(text.substring(0, visibleStart));
        
        for (int i = 0; i < text.length() - visibleStart - visibleEnd; i++) {
            masked.append("*");
        }
        
        masked.append(text.substring(text.length() - visibleEnd));
        
        return masked.toString();
    }
}