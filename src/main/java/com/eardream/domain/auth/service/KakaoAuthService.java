package com.eardream.domain.auth.service;

import com.eardream.domain.auth.dto.AuthResponse;
import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.service.UserService;
import com.eardream.global.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 카카오 OAuth 인증 서비스
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class KakaoAuthService {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${kakao.client-id}")
    private String kakaoClientId;
    
    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;
    
    @Value("${kakao.redirect-uri}")
    private String kakaoRedirectUri;
    
    private static final String KAKAO_AUTH_URL = "https://kauth.kakao.com/oauth/authorize";
    private static final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";
    
    // JWT 토큰 저장소 (실제 구현에서는 Redis 등 사용 권장)
    private final Map<String, String> tokenStorage = new HashMap<>();
    
    /**
     * 카카오 인증 URL 생성
     */
    public String getKakaoAuthUrl() {
        try {
            String encodedRedirectUri = URLEncoder.encode(kakaoRedirectUri, StandardCharsets.UTF_8);
            String state = generateRandomState();
            
            return KAKAO_AUTH_URL + 
                   "?client_id=" + kakaoClientId +
                   "&redirect_uri=" + encodedRedirectUri +
                   "&response_type=code" +
                   "&state=" + state;
        } catch (Exception e) {
            throw new RuntimeException("카카오 인증 URL 생성 실패", e);
        }
    }
    
    /**
     * 카카오 인증 코드로 JWT 토큰 발급
     */
    public AuthResponse authenticateWithKakao(String code) {
        try {
            // 1. 카카오 액세스 토큰 획득
            String kakaoAccessToken = getKakaoAccessToken(code);
            
            // 2. 카카오 사용자 정보 조회
            KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken);
            
            // 3. 사용자 조회 또는 생성
            UserDto user = findOrCreateUser(kakaoUserInfo);
            
            // 4. JWT 토큰 생성
            String accessToken = jwtTokenProvider.generateAccessToken(
                user.getUserId(), user.getKakaoId(), user.getName());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
            
            // 5. 토큰 저장 (실제로는 Redis 등 사용)
            tokenStorage.put(accessToken, user.getUserId().toString());
            
            // 6. 응답 생성
            long expiresIn = jwtTokenProvider.getAccessTokenExpiration() / 1000; // 초 단위
            boolean isNewUser = kakaoUserInfo.isNewUser;
            
            return isNewUser 
                ? AuthResponse.ofNewUser(accessToken, refreshToken, expiresIn, user)
                : AuthResponse.of(accessToken, refreshToken, expiresIn, user);
                
        } catch (Exception e) {
            throw new RuntimeException("카카오 인증 처리 실패: " + e.getMessage(), e);
        }
    }
    
    /**
     * 현재 사용자 정보 조회
     */
    public UserDto getCurrentUser(String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        return userService.getMyProfile(userId);
    }
    
    /**
     * JWT 토큰 갱신
     */
    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken) || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("유효하지 않은 Refresh Token입니다");
        }
        
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        UserDto user = userService.getMyProfile(userId);
        
        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(
            user.getUserId(), user.getKakaoId(), user.getName());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());
        
        // 토큰 저장소 업데이트
        tokenStorage.put(newAccessToken, user.getUserId().toString());
        
        long expiresIn = jwtTokenProvider.getAccessTokenExpiration() / 1000;
        return AuthResponse.of(newAccessToken, newRefreshToken, expiresIn, user);
    }
    
    /**
     * 로그아웃 - 토큰 무효화
     */
    @Transactional
    public void logout(String token) {
        // 토큰 저장소에서 제거 (실제로는 Redis에서 제거)
        tokenStorage.remove(token);
        
        // 추가로 카카오 로그아웃 API 호출할 수도 있음
    }
    
    // ===== Private Helper Methods =====
    
    /**
     * 카카오 액세스 토큰 획득
     */
    private String getKakaoAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("client_secret", kakaoClientSecret);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);
        
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(KAKAO_TOKEN_URL, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("access_token").asText();
            } else {
                throw new RuntimeException("카카오 토큰 획득 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 토큰 요청 처리 실패", e);
        }
    }
    
    /**
     * 카카오 사용자 정보 조회
     */
    private KakaoUserInfo getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                
                String kakaoId = jsonNode.get("id").asText();
                JsonNode properties = jsonNode.get("properties");
                JsonNode kakaoAccount = jsonNode.get("kakao_account");
                
                String profileImage = properties.has("profile_image")
                    ? properties.get("profile_image").asText() : null;
                String name = kakaoAccount.has("name")
                    ? kakaoAccount.get("name").asText() : null;
                String phoneNumber = kakaoAccount.has("phone_number")
                    ? kakaoAccount.get("phone_number").asText() : null;
                String birthYear = kakaoAccount.has("birthyear")
                    ? kakaoAccount.get("birthyear").asText() : null;
                String birthDay = kakaoAccount.has("birthday")
                    ? kakaoAccount.get("birthday").asText() : null;
                
                // birth date 안전하게 파싱
                LocalDate birthDate = null;
                if (birthYear != null && birthDay != null) {
                    try {
                        birthDate = new SimpleDateFormat("yyyyMMdd").parse(birthYear + birthDay)
                                .toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    } catch (Exception e) {
                        // 파싱 실패 시 null로 처리
                        birthDate = null;
                    }
                }
                
                return KakaoUserInfo.builder()
                        .kakaoId(kakaoId)
                        .name(name)
                        .phoneNumber(phoneNumber)
                        .birthDate(birthDate)
                        .profileImage(profileImage)
                        .build();
            } else {
                throw new RuntimeException("카카오 사용자 정보 조회 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("카카오 사용자 정보 요청 처리 실패", e);
        }
    }
    
    /**
     * 사용자 조회 또는 생성
     */
    public UserDto findOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        try {
            // 기존 사용자 조회
            return userService.getUserByKakaoId(kakaoUserInfo.kakaoId);
        } catch (IllegalArgumentException e) {
            // 신규 사용자 생성
            CreateUserRequest createRequest = CreateUserRequest.builder()
                    .kakaoId(kakaoUserInfo.kakaoId)
                    .name(kakaoUserInfo.name)
                    .birthDate(kakaoUserInfo.birthDate)
                    .phoneNumber(kakaoUserInfo.phoneNumber)
                    .profileImageUrl(kakaoUserInfo.profileImage)
                    .build();
            
            kakaoUserInfo.isNewUser = true;
            return userService.createUser(createRequest);
        }
    }
    
    /**
     * 랜덤 state 생성
     */
    private String generateRandomState() {
        return String.valueOf(System.currentTimeMillis());
    }
    
    // ===== Inner Classes =====
    
    /**
     * 카카오 사용자 정보 DTO
     */
    @Data
    @AllArgsConstructor
    @Builder
    private static class KakaoUserInfo {
        String kakaoId;
        String name;
        String phoneNumber;
        String birthYear;
        String birthDay;
        LocalDate birthDate;
        String profileImage;
        boolean isNewUser = false;
    }
}