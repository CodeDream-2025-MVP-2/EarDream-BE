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
public class InvitationDto {
	private Long id;
	private String inviteCode;
	private Long invitedUserId;
	private String status;
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private LocalDateTime acceptedAt;
}





