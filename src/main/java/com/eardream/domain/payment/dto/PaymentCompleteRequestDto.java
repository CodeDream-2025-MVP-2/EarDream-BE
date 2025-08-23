package com.eardream.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프론트엔드에서 결제 완료 후 전달하는 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompleteRequestDto {
    
    @NotBlank(message = "결제 ID는 필수입니다")
    private String paymentId;  // PortOne에서 발급한 결제 ID
    
    @NotBlank(message = "거래 ID는 필수입니다")
    private String merchantUid;  // 상점 거래 ID
    
    private String impUid;  // PortOne 거래 고유번호
    
    private String familyId;  // 가족 ID
    
    private String status;  // 결제 상태
}