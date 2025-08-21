package com.eardream.domain.families.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationReviewDto {
	private Long invitationId;
	private Long userId;
	private String name;
	private String profileImageUrl;
	private LocalDateTime requestedAt;
}





