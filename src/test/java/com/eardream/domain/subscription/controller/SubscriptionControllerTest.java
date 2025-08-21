package com.eardream.domain.subscription.controller;

import com.eardream.domain.subscription.dto.SubscriptionCreateRequest;
import com.eardream.domain.subscription.dto.SubscriptionResponse;
import com.eardream.domain.subscription.dto.SubscriptionUpdateRequest;
import com.eardream.domain.subscription.entity.Subscription;
import com.eardream.domain.subscription.service.SubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Subscription Controller API 테스트")
class SubscriptionControllerTest {

    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SubscriptionService subscriptionService;
    
    @InjectMocks
    private SubscriptionController subscriptionController;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController).build();
    }

    @Test
    @DisplayName("POST /subscriptions - 구독 시작 성공")
    void createSubscription_Success() throws Exception {
        // Given
        SubscriptionCreateRequest request = SubscriptionCreateRequest.builder()
                .familyId(1L)
                .planPrice(new BigDecimal("29900"))
                .inicisBillkey("INI_BILLKEY_12345")
                .build();

        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .familyId(1L)
                .planPrice(new BigDecimal("29900"))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .statusDescription("활성")
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .inicisBillkey("INI_BILLKEY_12345")
                .startedAt(LocalDateTime.now())
                .build();

        given(subscriptionService.createSubscription(any(SubscriptionCreateRequest.class)))
                .willReturn(response);

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("구독이 성공적으로 시작되었습니다."))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.familyId").value(1))
                .andExpect(jsonPath("$.data.planPrice").value(29900))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("GET /subscriptions/me - 내 구독 현황 조회 성공")
    void getMySubscription_Success() throws Exception {
        // Given
        Long familyId = 1L;
        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(1L)
                .familyId(familyId)
                .planPrice(new BigDecimal("29900"))
                .status(Subscription.SubscriptionStatus.ACTIVE)
                .statusDescription("활성")
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .inicisBillkey("INI_BILLKEY_12345")
                .startedAt(LocalDateTime.now())
                .build();

        given(subscriptionService.getMySubscription(familyId))
                .willReturn(response);

        // When & Then
        mockMvc.perform(get("/subscriptions/me")
                        .param("familyId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.familyId").value(1))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("PATCH /subscriptions/{id} - 구독 변경 성공")
    void updateSubscription_Success() throws Exception {
        // Given
        Long subscriptionId = 1L;
        SubscriptionUpdateRequest request = SubscriptionUpdateRequest.builder()
                .status(Subscription.SubscriptionStatus.PAUSED)
                .build();

        SubscriptionResponse response = SubscriptionResponse.builder()
                .id(subscriptionId)
                .familyId(1L)
                .planPrice(new BigDecimal("29900"))
                .status(Subscription.SubscriptionStatus.PAUSED)
                .statusDescription("일시중지")
                .nextBillingDate(LocalDate.now().plusMonths(1))
                .pauseStartedAt(LocalDateTime.now())
                .build();

        given(subscriptionService.updateSubscription(eq(subscriptionId), any(SubscriptionUpdateRequest.class)))
                .willReturn(response);

        // When & Then
        mockMvc.perform(patch("/subscriptions/{id}", subscriptionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("구독 정보가 성공적으로 변경되었습니다."))
                .andExpect(jsonPath("$.data.status").value("PAUSED"));
    }

    @Test
    @DisplayName("DELETE /subscriptions/{id} - 구독 취소 성공")
    void cancelSubscription_Success() throws Exception {
        // Given
        Long subscriptionId = 1L;
        doNothing().when(subscriptionService).cancelSubscription(subscriptionId);

        // When & Then
        mockMvc.perform(delete("/subscriptions/{id}", subscriptionId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("구독이 성공적으로 취소되었습니다."));
    }

    @Test
    @DisplayName("POST /subscriptions - 유효하지 않은 요청 데이터 검증")
    void createSubscription_InvalidRequest() throws Exception {
        // Given - 필수 필드 누락
        SubscriptionCreateRequest request = SubscriptionCreateRequest.builder()
                .planPrice(new BigDecimal("29900"))
                .build();

        // When & Then
        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}