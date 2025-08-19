package com.eardream.domain.user.entity;

/**
 * 사용자 유형 열거형
 */
public enum UserType {
    /**
     * 초대받은 사람 (회원가입 전 상태)
     * kakaoId가 null이며, 주로 받는 분(어르신) 정보
     */
    PENDING_RECIPIENT("PENDING_RECIPIENT", "초대받은 사람"),
    
    /**
     * 회원가입 완료된 활성 사용자
     * kakaoId가 설정되어 있고, 앱을 사용할 수 있는 상태
     */
    ACTIVE_USER("ACTIVE_USER", "활성 사용자");
    
    private final String code;
    private final String description;
    
    UserType(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 코드 문자열로부터 UserType을 찾는다
     */
    public static UserType fromCode(String code) {
        for (UserType type : UserType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown UserType code: " + code);
    }
    
    @Override
    public String toString() {
        return this.code;
    }
}