package com.eardream.domain.user.service;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.entity.User;
import com.eardream.domain.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService 단위 테스트")
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequest createRequest;
    private UpdateUserRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.createFromKakao("kakao123", "홍길동");
        testUser.setId(1L);
        testUser.updateProfile("홍길동", "010-1234-5678", "profile.jpg", 
                              LocalDate.of(1990, 1, 1), "서울시 강남구");

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
    @DisplayName("사용자 생성 - 성공")
    void createUser_Success() {
        // given
        when(userMapper.existsByKakaoId(anyString())).thenReturn(0);
        when(userMapper.insertUser(any(User.class))).thenReturn(1);

        // when
        UserDto result = userService.createUser(createRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getKakaoId()).isEqualTo("kakao456");
        assertThat(result.getName()).isEqualTo("김철수");
        assertThat(result.getPhoneNumber()).isEqualTo("010-9876-5432");
        verify(userMapper, times(1)).existsByKakaoId("kakao456");
        verify(userMapper, times(1)).insertUser(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - 중복된 Kakao ID로 실패")
    void createUser_DuplicateKakaoId_Fail() {
        // given
        when(userMapper.existsByKakaoId(anyString())).thenReturn(1);

        // when & then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 Kakao ID입니다");
        
        verify(userMapper, times(1)).existsByKakaoId("kakao456");
        verify(userMapper, never()).insertUser(any(User.class));
    }

    @Test
    @DisplayName("사용자 생성 - DB 저장 실패")
    void createUser_DBInsertFail() {
        // given
        when(userMapper.existsByKakaoId(anyString())).thenReturn(0);
        when(userMapper.insertUser(any(User.class))).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> userService.createUser(createRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 생성에 실패했습니다");
    }

    @Test
    @DisplayName("내 프로필 조회 - 성공")
    void getMyProfile_Success() {
        // given
        when(userMapper.findById(1L)).thenReturn(Optional.of(testUser));

        // when
        UserDto result = userService.getMyProfile(1L);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("홍길동");
        assertThat(result.getKakaoId()).isEqualTo("kakao123");
        verify(userMapper, times(1)).findById(1L);
    }

    @Test
    @DisplayName("내 프로필 조회 - 사용자 없음으로 실패")
    void getMyProfile_UserNotFound() {
        // given
        when(userMapper.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getMyProfile(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("Kakao ID로 사용자 조회 - 성공")
    void getUserByKakaoId_Success() {
        // given
        when(userMapper.findByKakaoId("kakao123")).thenReturn(Optional.of(testUser));

        // when
        UserDto result = userService.getUserByKakaoId("kakao123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getKakaoId()).isEqualTo("kakao123");
        assertThat(result.getName()).isEqualTo("홍길동");
        verify(userMapper, times(1)).findByKakaoId("kakao123");
    }

    @Test
    @DisplayName("Kakao ID로 사용자 조회 - 사용자 없음")
    void getUserByKakaoId_NotFound() {
        // given
        when(userMapper.findByKakaoId("unknown")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.getUserByKakaoId("unknown"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("Kakao ID 존재 여부 확인 - 존재함")
    void existsByKakaoId_Exists() {
        // given
        when(userMapper.existsByKakaoId("kakao123")).thenReturn(1);

        // when
        boolean exists = userService.existsByKakaoId("kakao123");

        // then
        assertThat(exists).isTrue();
        verify(userMapper, times(1)).existsByKakaoId("kakao123");
    }

    @Test
    @DisplayName("Kakao ID 존재 여부 확인 - 존재하지 않음")
    void existsByKakaoId_NotExists() {
        // given
        when(userMapper.existsByKakaoId("unknown")).thenReturn(0);

        // when
        boolean exists = userService.existsByKakaoId("unknown");

        // then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("프로필 수정 - 성공")
    void updateMyProfile_Success() {
        // given
        when(userMapper.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.updateUser(any(User.class))).thenReturn(1);

        // when
        UserDto result = userService.updateMyProfile(1L, updateRequest);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("홍길순");
        assertThat(result.getPhoneNumber()).isEqualTo("010-1111-2222");
        assertThat(result.getProfileImageUrl()).isEqualTo("new_profile.jpg");
        verify(userMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).updateUser(any(User.class));
    }

    @Test
    @DisplayName("프로필 수정 - 변경 사항 없음")
    void updateMyProfile_NoChanges() {
        // given
        UpdateUserRequest emptyRequest = UpdateUserRequest.builder().build();

        // when & then
        assertThatThrownBy(() -> userService.updateMyProfile(1L, emptyRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("수정할 정보가 없습니다");
        
        verify(userMapper, never()).findById(anyLong());
        verify(userMapper, never()).updateUser(any(User.class));
    }

    @Test
    @DisplayName("프로필 수정 - 사용자 없음")
    void updateMyProfile_UserNotFound() {
        // given
        when(userMapper.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.updateMyProfile(999L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("프로필 수정 - DB 업데이트 실패")
    void updateMyProfile_DBUpdateFail() {
        // given
        when(userMapper.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.updateUser(any(User.class))).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> userService.updateMyProfile(1L, updateRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자 정보 수정에 실패했습니다");
    }

    @Test
    @DisplayName("계정 삭제 - 성공")
    void deleteAccount_Success() {
        // given
        when(userMapper.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.deleteById(1L)).thenReturn(1);

        // when
        userService.deleteAccount(1L);

        // then
        verify(userMapper, times(1)).findById(1L);
        verify(userMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("계정 삭제 - 사용자 없음")
    void deleteAccount_UserNotFound() {
        // given
        when(userMapper.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.deleteAccount(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("사용자를 찾을 수 없습니다");
        
        verify(userMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("계정 삭제 - DB 삭제 실패")
    void deleteAccount_DBDeleteFail() {
        // given
        when(userMapper.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.deleteById(1L)).thenReturn(0);

        // when & then
        assertThatThrownBy(() -> userService.deleteAccount(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("계정 삭제에 실패했습니다");
    }
}