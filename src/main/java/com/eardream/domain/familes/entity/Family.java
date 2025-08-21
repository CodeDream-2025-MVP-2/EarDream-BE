package com.eardream.domain.familes.entity;

import lombok.AllArgsConstructor;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Family {

	private Long id;
	private Long userId;
	private String familyName;
	private String familyProfileImageUrl;
	private Integer monthlyDeadline;
	private String inviteCode;
	private String status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}


