package com.eardream.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 빌링키 정보 엔티티 (정기결제용)
 * 포트원 V2 SDK 빌링키 응답과 연동
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKey {
    
    /** 빌링키 ID (Primary Key) */
    private String billingKeyId;
    
    /** 사용자 ID (Foreign Key) */
    private String userId;
    
    /** 가족 그룹 ID (Foreign Key) */
    private String familyId;
    
    /** 고객 고유 ID (PortOne에서 사용) */
    private String customerUid;
    
    /** 포트원 빌링키 */
    private String portoneKey;
    
    /** PG사 코드 */
    private String pgProvider;
    
    /** 카드사명 */
    private String cardName;
    
    /** 마스킹된 카드번호 (ex: 1234-****-****-5678) */
    private String cardNumber;
    
    /** 카드 타입 (CREDIT, DEBIT 등) */
    private String cardType;

    /** 카드사 */
    private String cardCompany;
    
    /** 결제 수단 정보 (JSON 형태로 추가 정보 저장) */
    private String paymentMethodInfo;
    
    /** 상태 */
    private BillingKeyStatus status;
    
    /** 발급 일시 */
    private LocalDateTime issuedAt;
    
    /** 만료 일시 */
    private LocalDateTime expiredAt;
    
    /** 마지막 사용 일시 */
    private LocalDateTime lastUsedAt;
    
    /** 생성일시 */
    private LocalDateTime createdAt;
    
    /** 수정일시 */
    private LocalDateTime updatedAt;
    
    /**
     * 빌링키 상태 enum
     */
    public enum BillingKeyStatus {
        ACTIVE,     // 활성
        INACTIVE,   // 비활성
        EXPIRED,    // 만료
        DELETED     // 삭제
    }
}