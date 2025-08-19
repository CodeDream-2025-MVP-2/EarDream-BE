package com.eardream.domain.user.mapper;

import com.eardream.domain.user.entity.User;
import com.eardream.domain.user.entity.UserType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * User 도메인 MyBatis Mapper 인터페이스
 */
@Mapper
public interface UserMapper {
    
    /**
     * 사용자 생성
     */
    int insertUser(User user);
    
    /**
     * ID로 사용자 조회
     */
    Optional<User> findById(@Param("id") Long id);
    
    /**
     * Kakao ID로 사용자 조회
     */
    Optional<User> findByKakaoId(@Param("kakaoId") String kakaoId);
    
    /**
     * 전화번호로 사용자 조회
     */
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 사용자 유형별 사용자 목록 조회
     */
    List<User> findByUserType(@Param("userType") UserType userType);
    
    /**
     * 가족 리더 목록 조회
     */
    List<User> findFamilyLeaders();
    
    /**
     * 소식지 수신자 목록 조회
     */
    List<User> findNewsletterReceivers();
    
    /**
     * 사용자 정보 수정
     */
    int updateUser(User user);
    
    /**
     * 사용자 리더 권한 변경
     */
    int updateLeaderStatus(@Param("id") Long id, @Param("isLeader") Boolean isLeader);
    
    /**
     * 사용자 수신자 상태 변경
     */
    int updateReceiverStatus(@Param("id") Long id, @Param("isReceiver") Boolean isReceiver);
    
    /**
     * 사용자 삭제 (Soft Delete는 구현하지 않음 - MVP 단순화)
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 전체 사용자 수 조회
     */
    int countUsers();
    
    /**
     * 사용자 유형별 수 조회
     */
    int countByUserType(@Param("userType") UserType userType);
    
    /**
     * 사용자 존재 여부 확인 - Kakao ID
     */
    int existsByKakaoId(@Param("kakaoId") String kakaoId);
    
    /**
     * 사용자 존재 여부 확인 - 전화번호
     */
    int existsByPhoneNumber(@Param("phoneNumber") String phoneNumber);
    
    /**
     * 최근 생성된 사용자 목록 조회 (관리용)
     */
    List<User> findRecentUsers(@Param("limit") int limit);
}