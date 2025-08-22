package com.eardream.domain.auth.controller;

import com.eardream.domain.auth.dto.KakaoAuthRequest;
import com.eardream.domain.auth.dto.AuthResponse;
import com.eardream.domain.auth.service.KakaoAuthService;
import com.eardream.global.common.ApiResponse;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Auth", description = "인증 및 토큰 발급/갱신/로그아웃")
public class AuthController {
    
    private final KakaoAuthService kakaoAuthService;
    
    /**
<<<<<<< HEAD
     * 카카오 로그인 시작 - 카카오 인증 서버로 리다이렉트
     */
    @GetMapping("/kakao")
    @Operation(summary = "카카오 로그인 URL", description = "카카오 인증을 시작할 수 있는 URL을 반환합니다.")
    public ResponseEntity<ApiResponse<String>> kakaoLogin() {
        String kakaoAuthUrl = kakaoAuthService.getKakaoAuthUrl();
        return ResponseEntity.ok(ApiResponse.success(kakaoAuthUrl, "카카오 로그인 URL"));
    }
    /**
=======
>>>>>>> ec92de7231e6b993e41af2a3ad9b09da718c18a3
     * 카카오 콜백 처리 - 인증 코드를 받아 JWT 토큰 발급
     */
    @PostMapping("/kakao/token")
    @Operation(summary = "카카오 콜백 처리", description = "카카오 인증코드를 받아 EarDream JWT를 발급합니다.")
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
    @Operation(summary = "로그아웃", description = "현재 토큰을 무효화합니다.")
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