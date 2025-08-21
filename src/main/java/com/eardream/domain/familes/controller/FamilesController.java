package com.eardream.domain.familes.controller;

import com.eardream.domain.familes.dto.CreateFamilyRequest;
import com.eardream.domain.familes.dto.FamilyDto;
import com.eardream.domain.familes.service.FamilyService;
import com.eardream.global.common.ApiResponse;
import com.eardream.global.common.PageRequest;
import com.eardream.global.common.PageResponse;
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
	
}
