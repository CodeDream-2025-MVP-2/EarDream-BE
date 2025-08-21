package com.eardream.domain.families.service;

import com.eardream.domain.families.mapper.FamilyMapper;
import com.eardream.domain.families.dto.CreateFamilyRequest;
import com.eardream.domain.families.dto.FamilyDto;
import com.eardream.domain.families.dto.InvitationDto;
import com.eardream.domain.families.dto.InviteIssueResponse;
import com.eardream.domain.families.dto.JoinRequest;
import com.eardream.domain.families.entity.Family;
import com.eardream.domain.families.entity.Invitation;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.service.UserService;
import com.eardream.global.common.PageRequest;
import com.eardream.global.common.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FamilyService {

	private final FamilyMapper familyMapper;
	private final UserService userService;

	@Transactional(readOnly = false)
	public FamilyDto createFamily(CreateFamilyRequest request) {
		Family family = new Family();

		family.setFamilyName(request.getFamilyName());
		family.setFamilyProfileImageUrl(request.getFamilyProfileImageUrl());
		family.setMonthlyDeadline(request.getMonthlyDeadline());
		family.setUserId(request.getUserId());
		// 초대코드는 최초 발급 시점에 생성 (없을 때만 생성)
		family.setInviteCode(null);
		family.setStatus("ACTIVE");
		family.setCreatedAt(LocalDateTime.now());
		family.setUpdatedAt(LocalDateTime.now());

		int result = familyMapper.insertFamily(family);

		if (result == 0) {
			throw new RuntimeException("가족 생성에 실패했습니다");
		}

		// 생성자도 가족 멤버로 등록 (리더)
		familyMapper.insertFamilyMember(
				family.getId(),
				family.getUserId(),
				"LEADER",   // relationship (초기값)
				"LEADER"    // role
		);

		return toDto(family);
	}

	public FamilyDto getFamilyByUserId(Long userId) {
		Family family = familyMapper.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("가족을 찾을 수 없습니다: userId=" + userId));
		return toDto(family);
	}

	public PageResponse<FamilyDto> getFamilies(PageRequest pageRequest) {
		int total = familyMapper.countAll();
		List<Family> list = familyMapper.findAll(pageRequest.getOffset(), pageRequest.getSize());
		List<FamilyDto> content = list.stream().map(this::toDto).collect(Collectors.toList());
		return PageResponse.of(content, pageRequest.getPage(), pageRequest.getSize(), total);
	}

	public List<com.eardream.domain.families.dto.FamilyMemberDto> getMembers(Long familyId) {
		return familyMapper.findMembersByFamilyId(familyId);
	}

	public java.util.List<com.eardream.domain.families.dto.InvitationReviewDto> getPendingInvitations(Long familyId) {
		return familyMapper.findPendingInvitations(familyId);
	}

	@Transactional(readOnly = false)
	public void approveInvitation(Long familyId, Long invitationId, String relationship) {
		// 초대 정보 조회 (familyId 일치 여부 확인)
		var invitation = familyMapper.findInvitationById(invitationId)
				.orElseThrow(() -> new IllegalArgumentException("초대 정보를 찾을 수 없습니다"));
		if (!invitation.getFamilyId().equals(familyId)) {
			throw new IllegalArgumentException("가족 ID가 일치하지 않습니다");
		}

		// 초대 상태 ACCEPTED로 업데이트
		int updated = familyMapper.approveInvitation(invitationId);
		if (updated == 0) throw new IllegalArgumentException("유효하지 않은 초대입니다");

		// 멤버 insert
		familyMapper.insertFamilyMember(familyId, invitation.getInvitedUserId(), relationship, "MEMBER");
	}

	@Transactional(readOnly = false)
	public void rejectInvitation(Long invitationId) {
		familyMapper.rejectInvitation(invitationId);
	}

	@Transactional(readOnly = false)
	public void removeMember(Long familyId, Long userId) {

		int deleted = familyMapper.deleteFamilyMember(familyId, userId);
		if (deleted == 0) {
			throw new IllegalArgumentException("해당 멤버를 찾을 수 없습니다");
		}
	}

	private String generateInviteCode() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6).toUpperCase();
	}

	/**
	 * DB를 조회하여 겹치지 않는 초대 코드를 생성합니다.
	 */
	private String generateUniqueInviteCode() {
		String code;
		int attempts = 0;
		final int maxAttempts = 5;
		do {
			code = generateInviteCode();
			attempts++;
			if (attempts > maxAttempts) {
				throw new IllegalStateException("초대 코드 생성에 실패했습니다. 다시 시도해주세요.");
			}
		} while (familyMapper.findByInviteCode(code).isPresent());
		return code;
	}

	@Transactional(readOnly = true)
	public InviteIssueResponse issueInvite(Long familyId) {
		Family family = familyMapper.findById(familyId)
				.orElseThrow(() -> new IllegalArgumentException("가족을 찾을 수 없습니다: " + familyId));
		String code = family.getInviteCode();
		if (code == null || code.isBlank()) {
			code = generateUniqueInviteCode();
			familyMapper.updateFamilyInviteCode(familyId, code);
			family.setInviteCode(code);
		}
		return InviteIssueResponse.builder()
				.familyId(family.getId())
				.familyName(family.getFamilyName())
				.inviteCode(code)
				.build();
	}

	@Transactional(readOnly = false)
	public InvitationDto joinByInvite(JoinRequest request) {
		// 초대코드로 가족 찾기
		Family family = familyMapper.findByInviteCode(request.getInviteCode())
				.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대코드입니다"));

		// 사용자 조회 (카카오 로그인으로 생성된 사용자)
		UserDto user = userService.getUserById(request.getUserId());

		// 초대 생성 (PENDING)
		Invitation invitation = Invitation.builder()
				.familyId(family.getId())
				.inviteCode(family.getInviteCode())
				.invitedUserId(user.getUserId())
				.status("PENDING")
				.createdAt(LocalDateTime.now())
				.build();
		familyMapper.insertInvitation(invitation);

		return InvitationDto.builder()
				.id(invitation.getId())
				.inviteCode(invitation.getInviteCode())
				.invitedUserId(invitation.getInvitedUserId())
				.status("PENDING")
				.createdAt(invitation.getCreatedAt())
				.build();
	}

	private FamilyDto toDto(Family family) {
		return FamilyDto.builder()
				.familyId(family.getId())
				.familyName(family.getFamilyName())
				.familyProfileImageUrl(family.getFamilyProfileImageUrl())
				.monthlyDeadline(family.getMonthlyDeadline())
				.inviteCode(family.getInviteCode())
				.status(family.getStatus())
				.createdAt(family.getCreatedAt())
				.updatedAt(family.getUpdatedAt())
				.build();
	}
}




