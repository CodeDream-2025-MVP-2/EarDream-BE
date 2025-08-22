package com.eardream.domain.user.controller;

import com.eardream.domain.auth.service.KakaoAuthService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * User 도메인 REST API Controller
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    
    private final UserService userService;
    private final KakaoAuthService kakaoAuthService;

    /**
     * 사용자 생성
     * POST /api/v1/users
     */
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
     * 현재 사용자 정보 조회
     */
    @GetMapping("/me")
    @Operation(summary = "현재 사용자 조회", description = "Authorization 헤더의 JWT로 현재 사용자 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Object>> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            Object userInfo = kakaoAuthService.getCurrentUser(token);
            return ResponseEntity.ok(ApiResponse.success(userInfo, "사용자 정보 조회 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("INVALID_TOKEN", "유효하지 않은 토큰입니다: " + e.getMessage()));
        }
    }


    /**
     * 사용자 정보 수정
     * PUT /api/v1/users/{id}
     */
    @PatchMapping("/me")
    @Operation(summary = "사용자 정보 수정", description = "현재 사용자 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<UserDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserDto user = userService.updateUser(id, request);
            return ResponseEntity.ok(ApiResponse.success(user, "사용자 정보가 성공적으로 수정되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_UPDATE_INVALID", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_UPDATE_FAILED", "사용자 정보 수정 중 오류가 발생했습니다"));
        }
    }

    /**
     * 사용자 삭제
     * DELETE /api/v1/users/{id}
     */
    @PatchMapping("/delete")
    @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다.")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.success(null, "사용자가 성공적으로 삭제되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_DELETE_FAILED", "사용자 삭제 중 오류가 발생했습니다"));
        }
    }
}