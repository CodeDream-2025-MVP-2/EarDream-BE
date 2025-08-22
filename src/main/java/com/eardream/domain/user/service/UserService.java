package com.eardream.domain.user.service;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.entity.User;
import com.eardream.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import com.eardream.global.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 서비스 (API 명세서 기반)
 * 카카오 OAuth 기반 사용자 관리
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    
    private final UserMapper userMapper;
    
    /**
     * 카카오 OAuth 회원가입 또는 프로필 완성
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        log.info("사용자 생성 요청 - kakaoId: {}, name: {}", request.getKakaoId(), request.getName());
        
        // 카카오 ID 중복 체크
        if (userMapper.existsByKakaoId(request.getKakaoId()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 Kakao ID입니다");
        }
        
        // User 엔티티 생성
        User user = User.createFromKakao(request.getKakaoId(), request.getName());
        if (request.getPhoneNumber() != null || request.getProfileImageUrl() != null ||
            request.getBirthDate() != null || request.getAddress() != null) {
            user.updateProfile(request.getName(), request.getPhoneNumber(),
                             request.getProfileImageUrl(), request.getBirthDate(), request.getAddress());
        }
        
        // 저장
        int result = userMapper.insertUser(user);
        if (result == 0) {
            throw new RuntimeException("사용자 생성에 실패했습니다");
        }
        
        log.info("사용자 생성 완료 - userId: {}", user.getId());
        return UserDto.from(user);
    }
    
    /**
     * 내 프로필 조회 (GET /users/me)
     */
    public UserDto getMyProfile(Long userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return UserDto.from(user);
    }
    
    /**
     * 카카오 ID로 사용자 조회 (로그인 시 사용)
     */
    public UserDto getUserByKakaoId(String kakaoId) {
        User user = userMapper.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        return UserDto.from(user);
    }
    
    /**
     * 카카오 ID 존재 여부 확인
     */
    public boolean existsByKakaoId(String kakaoId) {
        return userMapper.existsByKakaoId(kakaoId) > 0;
    }
    
    /**
     * 내 프로필 수정 (PATCH /users/me)
     */
    @Transactional
    public UserDto updateMyProfile(Long userId, UpdateUserRequest request) {
        log.info("사용자 프로필 수정 요청 - userId: {}", userId);

        if (!request.hasChanges()) {
            throw new IllegalArgumentException("수정할 정보가 없습니다");
        }
        
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
        
        // 프로필 정보 업데이트
        user.updateProfile(
            request.getName() != null ? request.getName() : user.getName(),
            request.getPhoneNumber() != null ? request.getPhoneNumber() : user.getPhoneNumber(),
            request.getProfileImageUrl() != null ? request.getProfileImageUrl() : user.getProfileImageUrl(),
            request.getBirthDate() != null ? request.getBirthDate() : user.getBirthDate(),
            request.getAddress() != null ? request.getAddress() : user.getAddress()
        );
        
        // 데이터베이스 업데이트
        int result = userMapper.updateUser(user);
        if (result == 0) {
            throw new RuntimeException("사용자 정보 수정에 실패했습니다");
        }
        
        log.info("사용자 프로필 수정 완료 - userId: {}", userId);
        return UserDto.from(user);
    }
    
    /**
     * 계정 삭제 (DELETE /users/delete)
     */
    @Transactional
    public void deleteAccount(Long userId) {
        log.info("계정 삭제 요청 - userId: {}", userId);
        
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        int result = userMapper.deleteById(userId);
        if (result == 0) {
            throw new RuntimeException("계정 삭제에 실패했습니다");
        }
        
        log.info("계정 삭제 완료 - userId: {}", userId);
    }
}