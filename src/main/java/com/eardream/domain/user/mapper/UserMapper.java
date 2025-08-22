package com.eardream.domain.user.mapper;

import com.eardream.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * User Mapper (schema.sql 기준)
 */
@Mapper
public interface UserMapper {
    
    /**
     * 사용자 생성 (INSERT)
     */
    int insertUser(User user);
    
    /**
     * ID로 사용자 조회
     */
    Optional<User> findById(@Param("id") Long id);
    
    /**
     * Kakao ID로 사용자 조회 (로그인 시 사용)
     */
    Optional<User> findByKakaoId(@Param("kakaoId") String kakaoId);
    
    /**
     * 사용자 정보 수정 (UPDATE)
     */
    int updateUser(User user);
    
    /**
     * 사용자 삭제 (DELETE)
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 카카오 ID 존재 여부 확인
     */
    int existsByKakaoId(@Param("kakaoId") String kakaoId);
}