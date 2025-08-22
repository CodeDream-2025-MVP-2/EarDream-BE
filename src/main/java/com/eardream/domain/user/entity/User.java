package com.eardream.domain.user.entity;

import lombok.*;
import com.eardream.domain.user.entity.UserType;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 및 받는 분 정보를 통합 관리하는 엔티티
 * 초대받은 사용자는 kakaoId가 null인 상태로 시작
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"phoneNumber", "address"}) // 민감 정보 제외
public class User {
    
    private Long id;                    // 사용자 내부 고유 ID (대체키, 자동증가)
    private String kakaoId;             // 카카오 사용자 고유 ID (회원가입 완료 시 설정)
    private String name;                // 사용자/받는 분 실명
    private String phoneNumber;         // 전화번호 (암호화 저장)
    private String profileImageUrl;     // 프로필 이미지 로컬 저장 경로
    private LocalDate birthDate;        // 생년월일
    private String address;             // 소식지 배송 주소 (암호화 저장, 받는 분인 경우 필수)
    private UserType userType;          // 사용자 유형 (예: ACTIVE_USER, PENDING_RECIPIENT)
    private String familyRole;          // (사용 안함) 가족 역할은 family_members에서 관리
    private Boolean isLeader;           // 가족 그룹 리더 여부 (DB 컬럼이 없을 수도 있음)
    private Boolean isReceiver;         // 소식지 수신자 여부 (DB 컬럼이 없을 수도 있음)
    private LocalDateTime createdAt;    // 등록일시
    private LocalDateTime updatedAt;    // 최종 수정일시
    
    
    /**
     * 팩토리 메서드 - 활성 사용자 생성
     */
    public static User createActiveUser(String kakaoId, String name, String phoneNumber) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .phoneNumber(phoneNumber)
                .userType(UserType.ACTIVE_USER)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    /**
     * 팩토리 메서드 - 초대받은 받는 분 생성
     */
    public static User createPendingRecipient(String name, String phoneNumber, String address) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .address(address)
                .userType(UserType.PENDING_RECIPIENT)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    // 비즈니스 메서드
    // userType/leader/receiver 관련 로직 제거
    
    public void updateProfile(String name, String phoneNumber, String profileImageUrl, LocalDate birthDate) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateFamilyRole(String familyRole) {
        this.familyRole = familyRole;
        this.updatedAt = LocalDateTime.now();
    }
    
}