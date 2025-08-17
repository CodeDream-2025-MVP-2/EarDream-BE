package com.eardream.domain.user.service;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.entity.User;
import com.eardream.domain.user.entity.UserType;
import com.eardream.domain.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User 도메인 비즈니스 로직 서비스
 */
@Service
@Transactional(readOnly = true)
public class UserService {
    
    private final UserMapper userMapper;
    
    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    /**
     * 사용자 생성
     */
    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        // 중복 검증
        if (request.getClerkId() != null && userMapper.existsByClerkId(request.getClerkId()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 Clerk ID입니다: " + request.getClerkId());
        }
        
        if (request.getPhoneNumber() != null && userMapper.existsByPhoneNumber(request.getPhoneNumber()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다: " + request.getPhoneNumber());
        }
        
        // 비즈니스 로직 검증
        if (UserType.ACTIVE_USER.equals(request.getUserType()) && !request.isValidForActiveUser()) {
            throw new IllegalArgumentException("활성 사용자는 Clerk ID가 필수입니다");
        }
        
        if (UserType.PENDING_RECIPIENT.equals(request.getUserType()) && !request.isValidForPendingRecipient()) {
            throw new IllegalArgumentException("초대받은 사용자는 주소가 필수입니다");
        }
        
        // User 엔티티 생성
        User user = createUserEntity(request);
        
        // 저장
        int result = userMapper.insertUser(user);
        if (result == 0) {
            throw new RuntimeException("사용자 생성에 실패했습니다");
        }
        
        return convertToDto(user);
    }
    
    /**
     * ID로 사용자 조회
     */
    public UserDto getUserById(Long id) {
        User user = userMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
        return convertToDto(user);
    }
    
    /**
     * Clerk ID로 사용자 조회
     */
    public UserDto getUserByClerkId(String clerkId) {
        User user = userMapper.findByClerkId(clerkId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + clerkId));
        return convertToDto(user);
    }
    
    /**
     * 전화번호로 사용자 조회
     */
    public UserDto getUserByPhoneNumber(String phoneNumber) {
        User user = userMapper.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + phoneNumber));
        return convertToDto(user);
    }
    
    /**
     * 사용자 유형별 목록 조회
     */
    public List<UserDto> getUsersByType(UserType userType) {
        List<User> users = userMapper.findByUserType(userType);
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 가족 리더 목록 조회
     */
    public List<UserDto> getFamilyLeaders() {
        List<User> leaders = userMapper.findFamilyLeaders();
        return leaders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 소식지 수신자 목록 조회
     */
    public List<UserDto> getNewsletterReceivers() {
        List<User> receivers = userMapper.findNewsletterReceivers();
        return receivers.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자 정보 수정
     */
    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        if (!request.hasChanges()) {
            throw new IllegalArgumentException("수정할 정보가 없습니다");
        }
        
        User existingUser = userMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id));
        
        // 전화번호 중복 확인 (본인 제외)
        if (request.getPhoneNumber() != null && 
            !request.getPhoneNumber().equals(existingUser.getPhoneNumber()) &&
            userMapper.existsByPhoneNumber(request.getPhoneNumber()) > 0) {
            throw new IllegalArgumentException("이미 존재하는 전화번호입니다: " + request.getPhoneNumber());
        }
        
        // 수정할 정보 적용
        applyUpdateRequest(existingUser, request);
        
        // 업데이트
        int result = userMapper.updateUser(existingUser);
        if (result == 0) {
            throw new RuntimeException("사용자 정보 수정에 실패했습니다");
        }
        
        return convertToDto(existingUser);
    }
    
    /**
     * 사용자 리더 권한 변경
     */
    @Transactional
    public void updateLeaderStatus(Long id, Boolean isLeader) {
        if (!userMapper.findById(id).isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
        }
        
        int result = userMapper.updateLeaderStatus(id, isLeader);
        if (result == 0) {
            throw new RuntimeException("리더 권한 변경에 실패했습니다");
        }
    }
    
    /**
     * 사용자 수신자 상태 변경
     */
    @Transactional
    public void updateReceiverStatus(Long id, Boolean isReceiver) {
        if (!userMapper.findById(id).isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
        }
        
        int result = userMapper.updateReceiverStatus(id, isReceiver);
        if (result == 0) {
            throw new RuntimeException("수신자 상태 변경에 실패했습니다");
        }
    }
    
    /**
     * 사용자 삭제
     */
    @Transactional
    public void deleteUser(Long id) {
        if (!userMapper.findById(id).isPresent()) {
            throw new IllegalArgumentException("사용자를 찾을 수 없습니다: " + id);
        }
        
        int result = userMapper.deleteById(id);
        if (result == 0) {
            throw new RuntimeException("사용자 삭제에 실패했습니다");
        }
    }
    
    /**
     * 사용자 존재 여부 확인 - Clerk ID
     */
    public boolean existsByClerkId(String clerkId) {
        return userMapper.existsByClerkId(clerkId) > 0;
    }
    
    /**
     * 사용자 존재 여부 확인 - 전화번호
     */
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userMapper.existsByPhoneNumber(phoneNumber) > 0;
    }
    
    /**
     * 전체 사용자 수 조회
     */
    public int getTotalUserCount() {
        return userMapper.countUsers();
    }
    
    /**
     * 사용자 유형별 수 조회
     */
    public int getUserCountByType(UserType userType) {
        return userMapper.countByUserType(userType);
    }
    
    /**
     * 최근 생성된 사용자 목록 조회
     */
    public List<UserDto> getRecentUsers(int limit) {
        List<User> users = userMapper.findRecentUsers(limit);
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    // ===== Private Helper Methods =====
    
    /**
     * CreateUserRequest를 User 엔티티로 변환
     */
    private User createUserEntity(CreateUserRequest request) {
        User user;
        
        if (UserType.ACTIVE_USER.equals(request.getUserType())) {
            user = User.createActiveUser(request.getClerkId(), request.getName(), request.getPhoneNumber());
        } else {
            user = User.createPendingRecipient(request.getName(), request.getPhoneNumber(), request.getAddress());
        }
        
        // 추가 정보 설정
        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setBirthDate(request.getBirthDate());
        user.setFamilyRole(request.getFamilyRole());
        user.setIsReceiver(request.getIsReceiver());
        
        return user;
    }
    
    /**
     * UpdateUserRequest를 기존 User 엔티티에 적용
     */
    private void applyUpdateRequest(User user, UpdateUserRequest request) {
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getFamilyRole() != null) {
            user.setFamilyRole(request.getFamilyRole());
        }
        if (request.getIsReceiver() != null) {
            user.setIsReceiver(request.getIsReceiver());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
    }
    
    /**
     * User 엔티티를 UserDto로 변환
     */
    private UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setUserId(user.getId());
        dto.setClerkId(user.getClerkId());
        dto.setName(user.getName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setProfileImageUrl(user.getProfileImageUrl());
        dto.setBirthDate(user.getBirthDate());
        dto.setAddress(user.getAddress());
        dto.setUserType(user.getUserType());
        dto.setFamilyRole(user.getFamilyRole());
        dto.setIsLeader(user.getIsLeader());
        dto.setIsReceiver(user.getIsReceiver());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}