package com.eardream.domain.families.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberDto {
	private Long id;
	private Long userId;
	private String name;
	private String profileImageUrl;
	private String relationship;
	private String role;
}





