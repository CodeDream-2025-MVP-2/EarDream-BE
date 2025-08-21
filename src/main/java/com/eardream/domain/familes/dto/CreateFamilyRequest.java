package com.eardream.domain.familes.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFamilyRequest {


	@NotBlank(message = "가족 이름은 필수입니다")
	@Size(max = 100, message = "가족 이름은 100자를 초과할 수 없습니다")
	private String familyName;

	@Size(max = 1000, message = "프로필 이미지 URL은 1000자를 초과할 수 없습니다")
	private String familyProfileImageUrl;

	@NotNull(message = "만든 사용자 ID는 필수입니다")
	private Long userId;


	@NotNull(message = "마감 주차는 필수입니다")
	private Integer monthlyDeadline;

	@AssertTrue(message = "마감 주차는 2 또는 4만 허용됩니다")
	private boolean isValidMonthlyDeadline() {
		return monthlyDeadline != null && (monthlyDeadline == 2 || monthlyDeadline == 4);
	}
}


