package com.eardream.domain.user.controller;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.service.UserService;
import com.eardream.global.jwt.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)  // Security 필터 비활성화
@DisplayName("UserController 통합 테스트")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    private UserDto testUserDto;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUserDto = UserDto.builder()
                .userId(1L)
                .kakaoId("kakao123")
                .name("홍길동")
                .phoneNumber("010-1234-5678")
                .profileImageUrl("profile.jpg")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("서울시 강남구")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateUserRequest.builder()
                .kakaoId("kakao456")
                .name("김철수")
                .phoneNumber("010-9876-5432")
                .profileImageUrl("profile2.jpg")
                .birthDate(LocalDate.of(1995, 5, 15))
                .address("서울시 서초구")
                .build();

        updateRequest = UpdateUserRequest.builder()
                .name("홍길순")
                .phoneNumber("010-1111-2222")
                .profileImageUrl("new_profile.jpg")
                .birthDate(LocalDate.of(1990, 2, 2))
                .address("서울시 송파구")
                .build();
    }

    @Test
    @DisplayName("POST /users - 사용자 생성 성공")
    void createUser_Success() throws Exception {
        // given
        UserDto createdUser = UserDto.builder()
                .userId(2L)
                .kakaoId("kakao456")
                .name("김철수")
                .phoneNumber("010-9876-5432")
                .profileImageUrl("profile2.jpg")
                .birthDate(LocalDate.of(1995, 5, 15))
                .address("서울시 서초구")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(createdUser);

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(2))
                .andExpect(jsonPath("$.data.kakaoId").value("kakao456"))
                .andExpect(jsonPath("$.data.name").value("김철수"))
                .andExpect(jsonPath("$.message").value("사용자가 성공적으로 생성되었습니다"));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /users - 유효성 검증 실패 (필수값 누락)")
    void createUser_ValidationFail() throws Exception {
        // given
        CreateUserRequest invalidRequest = CreateUserRequest.builder()
                .kakaoId(null) // 필수값 누락
                .name("김철수")
                .build();

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        verify(userService, never()).createUser(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("POST /users - 중복된 Kakao ID로 실패")
    void createUser_DuplicateKakaoId() throws Exception {
        // given
        when(userService.createUser(any(CreateUserRequest.class)))
                .thenThrow(new IllegalArgumentException("이미 존재하는 Kakao ID입니다"));

        // when & then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("USER_CREATE_INVALID"))
                .andExpect(jsonPath("$.message").value("이미 존재하는 Kakao ID입니다"));
    }

    @Test
    @DisplayName("GET /users/me - 내 프로필 조회 성공")
    void getMyProfile_Success() throws Exception {
        // given
        when(userService.getMyProfile(anyLong())).thenReturn(testUserDto);

        // when & then
        mockMvc.perform(get("/users/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.kakaoId").value("kakao123"))
                .andExpect(jsonPath("$.data.name").value("홍길동"));

        verify(userService, times(1)).getMyProfile(anyLong());
    }

    @Test
    @DisplayName("PATCH /users/me - 프로필 수정 성공")
    void updateMyProfile_Success() throws Exception {
        // given
        UserDto updatedUser = UserDto.builder()
                .userId(1L)
                .kakaoId("kakao123")
                .name("홍길순")
                .phoneNumber("010-1111-2222")
                .profileImageUrl("new_profile.jpg")
                .birthDate(LocalDate.of(1990, 2, 2))
                .address("서울시 송파구")
                .createdAt(testUserDto.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userService.updateMyProfile(anyLong(), any(UpdateUserRequest.class)))
                .thenReturn(updatedUser);

        // when & then
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("홍길순"))
                .andExpect(jsonPath("$.data.phoneNumber").value("010-1111-2222"))
                .andExpect(jsonPath("$.data.profileImageUrl").value("new_profile.jpg"))
                .andExpect(jsonPath("$.message").value("프로필이 성공적으로 수정되었습니다"));

        verify(userService, times(1)).updateMyProfile(anyLong(), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("PATCH /users/me - 빈 요청으로 수정 시도")
    void updateMyProfile_EmptyRequest() throws Exception {
        // given
        UpdateUserRequest emptyRequest = UpdateUserRequest.builder().build();

        // when & then
        mockMvc.perform(patch("/users/me")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyRequest)))
                .andDo(print())
                .andExpect(status().isOk()); // Controller는 빈 요청도 허용, Service에서 검증

        verify(userService, times(1)).updateMyProfile(anyLong(), any(UpdateUserRequest.class));
    }

    @Test
    @DisplayName("DELETE /users/delete - 계정 삭제 성공")
    void deleteAccount_Success() throws Exception {
        // given
        doNothing().when(userService).deleteAccount(anyLong());

        // when & then
        mockMvc.perform(delete("/users/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("계정이 성공적으로 삭제되었습니다"));

        verify(userService, times(1)).deleteAccount(anyLong());
    }

    @Test
    @DisplayName("DELETE /users/delete - 존재하지 않는 사용자 삭제 시도")
    void deleteAccount_UserNotFound() throws Exception {
        // given
        doThrow(new IllegalArgumentException("사용자를 찾을 수 없습니다"))
                .when(userService).deleteAccount(anyLong());

        // when & then
        mockMvc.perform(delete("/users/delete")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError()) // GlobalExceptionHandler가 500 반환
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"));
    }
}