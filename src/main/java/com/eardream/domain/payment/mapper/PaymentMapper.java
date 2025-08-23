package com.eardream.domain.payment.mapper;

import com.eardream.domain.payment.entity.BillingKey;
import com.eardream.domain.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * 결제 관련 MyBatis Mapper 인터페이스
 */
@Mapper
public interface PaymentMapper {

    // Payment 관련 메서드
    
    /**
     * 결제 정보 저장
     */
    void insertPayment(Payment payment);

    /**
     * 결제 정보 업데이트
     */
    void updatePayment(Payment payment);

    /**
     * 결제 ID로 결제 정보 조회
     */
    Optional<Payment> findPaymentById(@Param("paymentId") String paymentId);

    /**
     * 주문 ID로 결제 정보 조회
     */
    Optional<Payment> findPaymentByOrderId(@Param("orderId") String orderId);

    /**
     * 포트원 거래 ID로 결제 정보 조회
     */
    Optional<Payment> findPaymentByPortoneTransactionId(@Param("portoneTransactionId") String portoneTransactionId);

    /**
     * 사용자 ID로 결제 내역 조회 (페이징)
     */
    List<Payment> findPaymentsByUserId(@Param("userId") String userId, 
                                      @Param("offset") int offset, 
                                      @Param("size") int size);

    /**
     * 가족 ID로 결제 내역 조회 (페이징)
     */
    List<Payment> findPaymentsByFamilyId(@Param("familyId") String familyId, 
                                        @Param("offset") int offset, 
                                        @Param("size") int size);

    /**
     * 결제 상태별 결제 내역 조회
     */
    List<Payment> findPaymentsByStatus(@Param("status") Payment.PaymentStatus status,
                                      @Param("offset") int offset, 
                                      @Param("size") int size);

    /**
     * 사용자의 결제 내역 개수 조회
     */
    int countPaymentsByUserId(@Param("userId") String userId);

    // BillingKey 관련 메서드
    
    /**
     * 빌링키 정보 저장
     */
    void insertBillingKey(BillingKey billingKey);

    /**
     * 빌링키 정보 업데이트
     */
    void updateBillingKey(BillingKey billingKey);

    /**
     * 빌링키 ID로 빌링키 정보 조회
     */
    Optional<BillingKey> findBillingKeyById(@Param("billingKeyId") String billingKeyId);

    /**
     * 고객 UID로 빌링키 존재 여부 확인
     */
    boolean existsBillingKeyByCustomerUid(@Param("customerUid") String customerUid);
    
    /**
     * 가족 ID로 활성 빌링키 조회
     */
    Optional<BillingKey> findActiveBillingKeyByFamilyId(@Param("familyId") String familyId);

    /**
     * 사용자 ID로 빌링키 목록 조회
     */
    List<BillingKey> findBillingKeysByUserId(@Param("userId") String userId);

    /**
     * 가족 ID로 빌링키 목록 조회
     */
    List<BillingKey> findBillingKeysByFamilyId(@Param("familyId") String familyId);

    /**
     * 활성 상태 빌링키 목록 조회
     */
    List<BillingKey> findActiveBillingKeysByUserId(@Param("userId") String userId);

    /**
     * 빌링키 삭제 (소프트 삭제)
     */
    void deleteBillingKey(@Param("billingKeyId") String billingKeyId);
}