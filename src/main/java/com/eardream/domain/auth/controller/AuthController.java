package com.eardream.domain.auth.controller;

import com.eardream.domain.auth.dto.KakaoAuthRequest;
import com.eardream.domain.auth.dto.AuthResponse;
import com.eardream.domain.auth.service.KakaoAuthService;
import com.eardream.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 인증 관련 컨트롤러 (Kakao OAuth + JWT)
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000"})
@RequiredArgsConstructor
public class AuthController {
    
    private final KakaoAuthService kakaoAuthService;
    
    /**
     * 카카오 콜백 처리 - 인증 코드를 받아 JWT 토큰 발급
     */
    @PostMapping("/kakao/token")
    public ResponseEntity<ApiResponse<AuthResponse>> kakaoCallback(@Valid @RequestBody KakaoAuthRequest request) {
        try {
            AuthResponse authResponse = kakaoAuthService.authenticateWithKakao(request.getCode());
            return ResponseEntity.ok(ApiResponse.success(authResponse, "인증 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("KAKAO_AUTH_FAILED", "카카오 인증 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * JWT 토큰 갱신
     */
    @PostMapping("/kakao/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            AuthResponse newTokens = kakaoAuthService.refreshToken(token);
            return ResponseEntity.ok(ApiResponse.success(newTokens, "토큰 갱신 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("TOKEN_REFRESH_FAILED", "토큰 갱신에 실패했습니다: " + e.getMessage()));
        }
    }
    
    /**
     * 로그아웃 - 토큰 무효화
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            kakaoAuthService.logout(token);
            return ResponseEntity.ok(ApiResponse.success("정상적으로 로그아웃되었습니다", "로그아웃 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("LOGOUT_FAILED", "로그아웃 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
}