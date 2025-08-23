package com.eardream.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트엔드에서 빌링키 발급 완료 후 전달하는 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingKeyRegisterRequestDto {
    
    @NotBlank(message = "빌링키는 필수입니다")
    private String billingKey;  // PortOne에서 발급한 빌링키
    
    @NotBlank(message = "고객 UID는 필수입니다")
    private String customerUid;  // 고객 고유 ID
    
    private String cardName;  // 카드사명
    
    private String cardNumber;  // 마스킹된 카드번호 (ex: 1234-****-****-5678)
    
    private String familyId;  // 가족 ID
}