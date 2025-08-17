package com.eardream.domain.user.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 및 받는 분 정보를 통합 관리하는 엔티티
 * 초대받은 사용자는 clerkId가 null인 상태로 시작
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"phoneNumber", "address"}) // 민감 정보 제외
public class User {
    
    private Long id;                    // 사용자 내부 고유 ID (대체키, 자동증가)
    private String clerkId;             // Clerk 사용자 고유 ID (회원가입 완료 시 설정)
    private String name;                // 사용자/받는 분 실명
    private String phoneNumber;         // 전화번호 (암호화 저장)
    private String profileImageUrl;     // 프로필 이미지 로컬 저장 경로
    private LocalDate birthDate;        // 생년월일
    private String address;             // 소식지 배송 주소 (암호화 저장, 받는 분인 경우 필수)
    private UserType userType;          // 사용자 유형
    private String familyRole;          // 가족 내 역할 (아들, 딸, 며느리, 사위 등)
    private Boolean isLeader;           // 가족 그룹 리더 여부
    private Boolean isReceiver;         // 소식지 수신자 여부
    private LocalDateTime createdAt;    // 등록일시
    private LocalDateTime updatedAt;    // 최종 수정일시
    
    
    // 생성자
    public User(String name, String phoneNumber, UserType userType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
        this.isLeader = false;
        this.isReceiver = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 팩토리 메서드 - 활성 사용자 생성
    public static User createActiveUser(String clerkId, String name, String phoneNumber) {
        User user = new User(name, phoneNumber, UserType.ACTIVE_USER);
        user.clerkId = clerkId;
        return user;
    }
    
    // 팩토리 메서드 - 초대받은 받는 분 생성
    public static User createPendingRecipient(String name, String phoneNumber, String address) {
        User user = new User(name, phoneNumber, UserType.PENDING_RECIPIENT);
        user.address = address;
        user.isReceiver = true;
        return user;
    }
    
    // 비즈니스 메서드
    public boolean isActiveUser() {
        return UserType.ACTIVE_USER.equals(this.userType);
    }
    
    public boolean isPendingRecipient() {
        return UserType.PENDING_RECIPIENT.equals(this.userType);
    }
    
    public boolean isFamilyLeader() {
        return Boolean.TRUE.equals(this.isLeader);
    }
    
    public boolean isNewsletterReceiver() {
        return Boolean.TRUE.equals(this.isReceiver);
    }
    
    public void promoteToLeader() {
        this.isLeader = true;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void demoteFromLeader() {
        this.isLeader = false;
        this.updatedAt = LocalDateTime.now();
    }
    
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