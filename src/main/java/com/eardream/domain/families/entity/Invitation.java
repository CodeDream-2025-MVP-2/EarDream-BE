package com.eardream.domain.families.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Invitation {
	private Long id;
	private Long familyId;
	private String inviteCode; // 6 chars fixed per family
	private Long invitedUserId; // nullable until joined
	private String status; // PENDING, ACCEPTED, EXPIRED
	private LocalDateTime expiresAt;
	private LocalDateTime createdAt;
	private LocalDateTime acceptedAt;
}





