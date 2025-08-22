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
public class FamilyMember {

	private Long id;
	private Long familyId;
	private Long userId;
	private String relationship;
	private String role; // LEADER, MEMBER
	private LocalDateTime joinedAt;
}





