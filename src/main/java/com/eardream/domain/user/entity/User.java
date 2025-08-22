package com.eardream.domain.user.entity;

import lombok.*;

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
    private String kakaoId;             // 카카오 사용자 고유 ID
    private String name;                // 사용자 실명
    private String phoneNumber;         // 전화번호 (암호화 저장)
    private String profileImageUrl;     // 프로필 이미지 로컬 저장 경로
    private LocalDate birthDate;        // 생년월일
    private String address;             // 소식지 배송 주소 (암호화 저장)
    private LocalDateTime createdAt;    // 등록일시
    private LocalDateTime updatedAt;    // 최종 수정일시

    /**
     * 카카오 OAuth 로그인으로 신규 사용자 생성
     */
    public static User createFromKakao(String kakaoId, String name) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
    
    /**
     * 프로필 정보 업데이트
     */
    public void updateProfile(String name, String phoneNumber, String profileImageUrl,
                            LocalDate birthDate, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.birthDate = birthDate;
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 기본 정보 업데이트 (이름, 전화번호만)
     */
    public void updateBasicInfo(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.updatedAt = LocalDateTime.now();
    }
}