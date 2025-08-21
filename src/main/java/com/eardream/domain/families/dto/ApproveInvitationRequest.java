package com.eardream.domain.families.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveInvitationRequest {

	@NotNull
	private Long invitationId;

	@NotBlank
	private String relationship; // 리더가 지정 (아들, 딸, 등)
}





