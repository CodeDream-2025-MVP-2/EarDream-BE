package com.eardream.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.eardream.domain.user.dto.UserDto;
import lombok.*;

/**
 * 인증 응답 DTO (JWT 토큰 + 사용자 정보)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @Builder.Default
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private Long expiresIn; // 토큰 만료 시간 (초)
    
    @JsonProperty("user_info")
    private UserDto userInfo;
    
    @Builder.Default
    @JsonProperty("is_new_user")
    private Boolean isNewUser = false; // 신규 가입자인지 여부
    
    /**
     * 일반 사용자 응답 생성
     */
    public static AuthResponse of(String accessToken, String refreshToken, Long expiresIn, UserDto userInfo) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .isNewUser(false)
                .build();
    }
    
    /**
     * 신규 사용자 응답 생성
     */
    public static AuthResponse ofNewUser(String accessToken, String refreshToken, Long expiresIn, UserDto userInfo) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .isNewUser(true)
                .build();
    }
}