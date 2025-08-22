package com.eardream.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 사용자 등록 요청 DTO (카카오 OAuth 콜백 후 프로필 설정)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {
    
    @NotBlank(message = "카카오 ID는 필수입니다")
    private String kakaoId;
    
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
}