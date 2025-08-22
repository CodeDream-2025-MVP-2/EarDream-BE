package com.eardream.domain.user.controller;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.service.UserService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * User API Controller (API 명세서 기반)
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;

    @PostMapping
    @Operation(summary = "사용자 생성", description = "사용자를 생성합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효성 오류")
    })
    public ResponseEntity<ApiResponse<UserDto>> createUser(@Valid @RequestBody CreateUserRequest request) {
        try {
            UserDto user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(user, "사용자가 성공적으로 생성되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_CREATE_INVALID", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_CREATE_FAILED", "사용자 생성 중 오류가 발생했습니다"));
        }
    }

    /**
     * 내 프로필 조회 (GET /users/me)
     */
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 조회", description = "Authorization 헤더의 JWT로 현재 사용자 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<UserDto>> getMyProfile() {
        // TODO: JWT에서 userId 추출하는 로직 필요
        Long userId = 1L; // 임시값
        
        log.debug("내 프로필 조회 요청 - userId: {}", userId);
        
        UserDto userDto = userService.getMyProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(userDto));
    }

    /**
     * 내 프로필 수정 (PATCH /users/me)
     */
    @PatchMapping("/me")
    @Operation(summary = "사용자 정보 수정", description = "현재 사용자 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<UserDto>> updateMyProfile(
            @Valid @RequestBody UpdateUserRequest request) {
        // TODO: JWT에서 userId 추출하는 로직 필요
        Long userId = 1L; // 임시값
        
        log.info("내 프로필 수정 요청 - userId: {}", userId);
        
        UserDto userDto = userService.updateMyProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(userDto, "프로필이 성공적으로 수정되었습니다"));
    }

    /**
     * 계정 삭제 (DELETE /users/delete)
     */
    @DeleteMapping("/delete")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteAccount() {
        // TODO: JWT에서 userId 추출하는 로직 필요
        Long userId = 1L; // 임시값
        
        log.info("계정 삭제 요청 - userId: {}", userId);
        
        userService.deleteAccount(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "계정이 성공적으로 삭제되었습니다"));
    }
}