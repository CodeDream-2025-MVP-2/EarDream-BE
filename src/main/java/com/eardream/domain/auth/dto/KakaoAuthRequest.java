package com.eardream.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 카카오 인증 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KakaoAuthRequest {
    
    @NotBlank(message = "인증 코드는 필수입니다")
    private String code;
    
    private String state;

    @Override
    public String toString() {
        return "KakaoAuthRequest{" +
                "code='" + code + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}