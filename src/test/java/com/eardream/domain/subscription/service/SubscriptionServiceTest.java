package com.eardream.domain.subscription.service;

import com.eardream.domain.subscription.dto.SubscriptionCreateRequest;
import com.eardream.domain.subscription.dto.SubscriptionResponse;
import com.eardream.domain.subscription.dto.SubscriptionUpdateRequest;
import com.eardream.domain.subscription.entity.Subscription;
import com.eardream.domain.subscription.mapper.SubscriptionMapper;
import com.eardream.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("Subscription Service 단위 테스트")
class SubscriptionServiceTest {

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Subscription mockSubscription;
    private SubscriptionCreateRequest createRequest;
    private SubscriptionUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        mockSubscription = Subscription.builder()
                .id(1L)
                .familyId(1L)
                .planPrice(new BigDecimal("29900"))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .inicisBillkey("INI_BILLKEY_12345")
                .startedAt(LocalDateTime.now())
                .build();

        createRequest = SubscriptionCreateRequest.builder()
                .familyId(1L)
                .planPrice(new BigDecimal("29900"))
                .inicisBillkey("INI_BILLKEY_12345")
                .build();

        updateRequest = SubscriptionUpdateRequest.builder()
                .status(Subscription.SubscriptionStatus.PAUSED)
                .planPrice(new BigDecimal("35000"))
                .build();
    }

    @Test
    @DisplayName("구독 생성 성공")
    void createSubscription_Success() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.empty());
        given(subscriptionMapper.insertSubscription(any(Subscription.class)))
                .willReturn(1);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFamilyId()).isEqualTo(1L);
        assertThat(result.getPlanPrice()).isEqualTo(new BigDecimal("29900"));
        assertThat(result.getStatus()).isEqualTo(Subscription.SubscriptionStatus.ACTIVE);

        verify(subscriptionMapper).findActiveByFamilyId(1L);
        verify(subscriptionMapper).insertSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("구독 생성 실패 - 이미 활성 구독 존재")
    void createSubscription_AlreadyExists() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.of(mockSubscription));

        // When & Then
        assertThatThrownBy(() -> subscriptionService.createSubscription(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 활성 구독이 존재합니다. familyId: 1");

        verify(subscriptionMapper).findActiveByFamilyId(1L);
        verify(subscriptionMapper, never()).insertSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("구독 생성 실패 - 데이터베이스 삽입 실패")
    void createSubscription_DatabaseError() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.empty());
        given(subscriptionMapper.insertSubscription(any(Subscription.class)))
                .willReturn(0); // 삽입 실패

        // When & Then
        assertThatThrownBy(() -> subscriptionService.createSubscription(createRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("구독 생성에 실패했습니다.");
    }

    @Test
    @DisplayName("내 구독 현황 조회 성공")
    void getMySubscription_Success() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.of(mockSubscription));

        // When
        SubscriptionResponse result = subscriptionService.getMySubscription(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFamilyId()).isEqualTo(1L);
        assertThat(result.getStatus()).isEqualTo(Subscription.SubscriptionStatus.ACTIVE);

        verify(subscriptionMapper).findActiveByFamilyId(1L);
    }

    @Test
    @DisplayName("내 구독 현황 조회 실패 - 구독 없음")
    void getMySubscription_NotFound() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subscriptionService.getMySubscription(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("활성 구독 정보를 찾을 수 없습니다. familyId: 1");

        verify(subscriptionMapper).findActiveByFamilyId(1L);
    }

    @Test
    @DisplayName("구독 ID로 조회 성공")
    void getSubscription_Success() {
        // Given
        given(subscriptionMapper.findById(1L))
                .willReturn(Optional.of(mockSubscription));

        // When
        SubscriptionResponse result = subscriptionService.getSubscription(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getFamilyId()).isEqualTo(1L);

        verify(subscriptionMapper).findById(1L);
    }

    @Test
    @DisplayName("구독 정보 수정 성공")
    void updateSubscription_Success() {
        // Given
        given(subscriptionMapper.findById(1L))
                .willReturn(Optional.of(mockSubscription));
        given(subscriptionMapper.updateSubscription(any(Subscription.class)))
                .willReturn(1);

        // When
        SubscriptionResponse result = subscriptionService.updateSubscription(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Subscription.SubscriptionStatus.PAUSED);
        assertThat(result.getPlanPrice()).isEqualTo(new BigDecimal("35000"));

        verify(subscriptionMapper).findById(1L);
        verify(subscriptionMapper).updateSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("구독 정보 수정 실패 - 구독 없음")
    void updateSubscription_NotFound() {
        // Given
        given(subscriptionMapper.findById(1L))
                .willReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subscriptionService.updateSubscription(1L, updateRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("구독 정보를 찾을 수 없습니다. id: 1");

        verify(subscriptionMapper).findById(1L);
        verify(subscriptionMapper, never()).updateSubscription(any(Subscription.class));
    }

    @Test
    @DisplayName("구독 취소 성공")
    void cancelSubscription_Success() {
        // Given
        given(subscriptionMapper.findById(1L))
                .willReturn(Optional.of(mockSubscription));
        given(subscriptionMapper.cancelSubscription(1L))
                .willReturn(1);

        // When
        subscriptionService.cancelSubscription(1L);

        // Then
        verify(subscriptionMapper).findById(1L);
        verify(subscriptionMapper).cancelSubscription(1L);
    }

    @Test
    @DisplayName("구독 취소 실패 - 활성 상태가 아님")
    void cancelSubscription_NotActive() {
        // Given
        Subscription cancelledSubscription = Subscription.builder()
                .id(1L)
                .familyId(1L)
                .status(Subscription.SubscriptionStatus.CANCELLED)
                .build();

        given(subscriptionMapper.findById(1L))
                .willReturn(Optional.of(cancelledSubscription));

        // When & Then
        assertThatThrownBy(() -> subscriptionService.cancelSubscription(1L))
                .isInstanceOf(BusinessException.class)
                .hasMessage("활성 상태의 구독만 취소할 수 있습니다.");

        verify(subscriptionMapper).findById(1L);
        verify(subscriptionMapper, never()).cancelSubscription(1L);
    }

    @Test
    @DisplayName("구독 일시중지 성공")
    void pauseSubscription_Success() {
        // Given
        given(subscriptionMapper.pauseSubscription(1L))
                .willReturn(1);

        // When
        subscriptionService.pauseSubscription(1L);

        // Then
        verify(subscriptionMapper).pauseSubscription(1L);
    }

    @Test
    @DisplayName("구독 재개 성공")
    void resumeSubscription_Success() {
        // Given
        given(subscriptionMapper.resumeSubscription(1L))
                .willReturn(1);

        // When
        subscriptionService.resumeSubscription(1L);

        // Then
        verify(subscriptionMapper).resumeSubscription(1L);
    }

    @Test
    @DisplayName("다음 결제일 계산 확인")
    void calculateNextBillingDate() {
        // Given
        given(subscriptionMapper.findActiveByFamilyId(1L))
                .willReturn(Optional.empty());
        given(subscriptionMapper.insertSubscription(any(Subscription.class)))
                .willReturn(1);

        // When
        SubscriptionResponse result = subscriptionService.createSubscription(createRequest);

        // Then
        assertThat(result.getNextBillingDate()).isAfter(LocalDate.now());
        assertThat(result.getNextBillingDate()).isEqualTo(LocalDate.now().plusMonths(1));
    }
}