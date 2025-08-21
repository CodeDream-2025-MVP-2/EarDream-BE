package com.eardream.domain.families.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InviteIssueResponse {
	private Long familyId;
	private String familyName;
	private String inviteCode; // 6 chars
}





