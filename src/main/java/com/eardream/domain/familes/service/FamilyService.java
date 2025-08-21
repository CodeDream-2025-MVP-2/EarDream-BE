package com.eardream.domain.familes.service;

import com.eardream.domain.familes.dto.CreateFamilyRequest;
import com.eardream.domain.familes.dto.FamilyDto;
import com.eardream.domain.familes.entity.Family;
import com.eardream.domain.familes.mapper.FamilyMapper;
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

	@Transactional(readOnly = false)
	public FamilyDto createFamily(CreateFamilyRequest request) {
		Family family = new Family();

		family.setFamilyName(request.getFamilyName());
		family.setFamilyProfileImageUrl(request.getFamilyProfileImageUrl());
		family.setMonthlyDeadline(request.getMonthlyDeadline());
		family.setUserId(request.getUserId());
		family.setInviteCode(generateUniqueInviteCode());
		family.setStatus("ACTIVE");
		family.setCreatedAt(LocalDateTime.now());
		family.setUpdatedAt(LocalDateTime.now());

		int result = familyMapper.insertFamily(family);

		if (result == 0) {
			throw new RuntimeException("가족 생성에 실패했습니다");
		}

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

	private String generateInviteCode() {
		return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10).toUpperCase();
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




