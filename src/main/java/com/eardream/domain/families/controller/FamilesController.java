package com.eardream.domain.families.controller;

import com.eardream.domain.families.dto.CreateFamilyRequest;
import com.eardream.domain.families.dto.FamilyDto;
import com.eardream.domain.families.service.FamilyService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
@Tag(name = "Family", description = "가족 그룹 관련 API")
public class FamilesController {

	private final FamilyService familyService;

    
	@PostMapping
	@Operation(summary = "가족 생성", description = "가족 그룹을 생성합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공",
					content = @Content(schema = @Schema(implementation = FamilyDto.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 오류")
	})
	public ResponseEntity<ApiResponse<FamilyDto>> createFamily(@Valid @RequestBody CreateFamilyRequest request) {
		FamilyDto created = familyService.createFamily(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "가족이 생성되었습니다"));
	}


	/*
	@GetMapping
	@Operation(summary = "가족 목록 조회", description = "가족 그룹 목록을 페이지네이션으로 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
					content = @Content(schema = @Schema(implementation = PageResponse.class)))
	})
	public ResponseEntity<ApiResponse<PageResponse<FamilyDto>>> getFamilies(
			@RequestParam(defaultValue = "1") int page,
			@RequestParam(defaultValue = "20") int size
	) {
		PageRequest pr = new PageRequest(page, size);
		PageResponse<FamilyDto> list = familyService.getFamilies(pr);
		return ResponseEntity.ok(ApiResponse.success(list));
	}*/

	@GetMapping("/user/{userId}")
	@Operation(summary = "가족 단건 조회", description = "만든 사람의 사용자 ID로 가족 정보를 조회합니다.")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공",
					content = @Content(schema = @Schema(implementation = FamilyDto.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "미존재 리소스")
	})
	public ResponseEntity<ApiResponse<FamilyDto>> getFamilyByUser(@PathVariable("userId") Long userId) {
		FamilyDto family = familyService.getFamilyByUserId(userId);
		return ResponseEntity.ok(ApiResponse.success(family));
	}


	@GetMapping("/{id}/members")
	@Operation(summary = "멤버 목록 (리더용)", description = "가족 그룹의 멤버 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<java.util.List<com.eardream.domain.families.dto.FamilyMemberDto>>> getMembers(@PathVariable("id") Long familyId) {
		var members = familyService.getMembers(familyId);
		return ResponseEntity.ok(ApiResponse.success(members));
	}
	

	@GetMapping("/{id}/invitations/pending")
	@Operation(summary = "초대 요청 목록", description = "리더가 검토할 대기 초대 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<java.util.List<com.eardream.domain.families.dto.InvitationReviewDto>>> getPendingInvitations(@PathVariable("id") Long familyId) {
		var list = familyService.getPendingInvitations(familyId);
		return ResponseEntity.ok(ApiResponse.success(list));
	}

	@PostMapping("/{id}/members/invite")
	@Operation(summary = "초대 발급", description = "가족의 초대코드가 없으면 생성하고, 코드와 가족정보를 반환합니다.")
	public ResponseEntity<ApiResponse<com.eardream.domain.families.dto.InviteIssueResponse>> issueInvite(@PathVariable("id") Long familyId) {
		var res = familyService.issueInvite(familyId);
		return ResponseEntity.ok(ApiResponse.success(res, "초대코드 발급"));
	}

	@PostMapping("/members/join")
	@Operation(summary = "초대 참여", description = "초대코드로 참여 신청을 합니다(사용자 생성 포함).")
	public ResponseEntity<ApiResponse<com.eardream.domain.families.dto.InvitationDto>> joinByInvite(@RequestBody com.eardream.domain.families.dto.JoinRequest request) {
		var res = familyService.joinByInvite(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(res, "참여 요청이 접수되었습니다"));
	}

	@PostMapping("/{id}/invitations/approve")
	@Operation(summary = "초대 승인", description = "리더가 관계를 지정하여 초대를 승인합니다.")
	public ResponseEntity<ApiResponse<Void>> approveInvitation(
			@PathVariable("id") Long familyId,
			@RequestBody com.eardream.domain.families.dto.ApproveInvitationRequest request
	) {
		familyService.approveInvitation(familyId, request.getInvitationId(), request.getRelationship());
		return ResponseEntity.ok(ApiResponse.success(null, "승인되었습니다"));
	}

	@PostMapping("/invitations/{invitationId}/reject")
	@Operation(summary = "초대 거부", description = "초대 요청을 거부합니다.")
	public ResponseEntity<ApiResponse<Void>> rejectInvitation(@PathVariable Long invitationId) {
		familyService.rejectInvitation(invitationId);
		return ResponseEntity.ok(ApiResponse.success(null, "거부되었습니다"));
	}

	@DeleteMapping("/{id}/members/{userId}")
	@Operation(summary = "멤버 내보내기", description = "가족 그룹에서 특정 멤버를 제거합니다.")
	public ResponseEntity<ApiResponse<Void>> removeMember(@PathVariable("id") Long familyId,
			@PathVariable("userId") Long userId) {
		familyService.removeMember(familyId, userId);
		return ResponseEntity.ok(ApiResponse.success(null, "멤버가 제거되었습니다"));
	}
	
}
