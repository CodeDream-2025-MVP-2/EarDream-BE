package com.eardream.domain.subscription.mapper;

import com.eardream.domain.subscription.entity.Subscription;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 구독 정보 MyBatis Mapper 인터페이스 (schema.sql 기반)
 */
@Mapper
public interface SubscriptionMapper {
    
    /**
     * 구독 정보 생성
     * @param subscription 구독 정보
     * @return 생성된 레코드 수
     */
    int insertSubscription(Subscription subscription);
    
    /**
     * 구독 ID로 구독 정보 조회
     * @param id 구독 ID
     * @return 구독 정보
     */
    Optional<Subscription> findById(@Param("id") Long id);
    
    /**
     * 가족 ID로 활성 구독 정보 조회
     * @param familyId 가족 ID
     * @return 활성 구독 정보
     */
    Optional<Subscription> findActiveByFamilyId(@Param("familyId") Long familyId);
    
    /**
     * 가족 ID로 모든 구독 이력 조회
     * @param familyId 가족 ID
     * @return 구독 이력 목록
     */
    List<Subscription> findAllByFamilyId(@Param("familyId") Long familyId);
    
    /**
     * 구독 정보 수정
     * @param subscription 수정할 구독 정보
     * @return 수정된 레코드 수
     */
    int updateSubscription(Subscription subscription);
    
    /**
     * 구독 상태 변경
     * @param id 구독 ID
     * @param status 변경할 상태
     * @return 수정된 레코드 수
     */
    int updateStatus(@Param("id") Long id, 
                    @Param("status") Subscription.SubscriptionStatus status);
    
    /**
     * 다음 결제일 업데이트
     * @param id 구독 ID
     * @param nextBillingDate 다음 결제일
     * @return 수정된 레코드 수
     */
    int updateNextBillingDate(@Param("id") Long id, 
                             @Param("nextBillingDate") LocalDate nextBillingDate);
    
    /**
     * 이니시스 빌링키 업데이트
     * @param id 구독 ID
     * @param inicisBillkey 이니시스 빌링키
     * @return 수정된 레코드 수
     */
    int updateInicisBillkey(@Param("id") Long id, 
                           @Param("inicisBillkey") String inicisBillkey);
    
    /**
     * 구독 삭제 (물리 삭제)
     * @param id 구독 ID
     * @return 삭제된 레코드 수
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 특정 날짜에 결제 예정인 구독 목록 조회
     * @param billingDate 결제 예정일
     * @return 결제 예정 구독 목록
     */
    List<Subscription> findByNextBillingDate(@Param("billingDate") LocalDate billingDate);
    
    /**
     * 구독 상태별 개수 조회
     * @param status 구독 상태
     * @return 해당 상태의 구독 개수
     */
    int countByStatus(@Param("status") Subscription.SubscriptionStatus status);
    
    /**
     * 전체 구독 개수 조회
     * @return 전체 구독 개수
     */
    int countAll();
    
    /**
     * 구독 해지 처리 (해지 일시 업데이트)
     * @param id 구독 ID
     * @return 수정된 레코드 수
     */
    int cancelSubscription(@Param("id") Long id);
    
    /**
     * 구독 일시정지 시작
     * @param id 구독 ID
     * @return 수정된 레코드 수
     */
    int pauseSubscription(@Param("id") Long id);
    
    /**
     * 구독 일시정지 종료 (재개)
     * @param id 구독 ID
     * @return 수정된 레코드 수
     */
    int resumeSubscription(@Param("id") Long id);
}