package com.eardream.domain.user.dto;

import com.eardream.domain.user.entity.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 사용자 생성 요청 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    private String name;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String phoneNumber;
    
    private String profileImageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @Size(max = 500, message = "주소는 500자를 초과할 수 없습니다")
    private String address;
    
    @NotNull(message = "사용자 유형은 필수입니다")
    private UserType userType;
    
    @Size(max = 50, message = "가족 역할은 50자를 초과할 수 없습니다")
    private String familyRole;
    
    @Builder.Default
    private Boolean isReceiver = false;
    
    // 카카오 ID (활성 사용자인 경우 필수)
    private String kakaoId;
    
    /**
     * 활성 사용자 유효성 검증
     */
    public boolean isValidForActiveUser() {
        return UserType.ACTIVE_USER.equals(userType) && kakaoId != null && !kakaoId.trim().isEmpty();
    }
    
    /**
     * 대기 중인 수신자 유효성 검증
     */
    public boolean isValidForPendingRecipient() {
        return UserType.PENDING_RECIPIENT.equals(userType) && address != null && !address.trim().isEmpty();
    }
}