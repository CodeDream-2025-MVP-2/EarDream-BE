package com.eardream.domain.user.controller;

import com.eardream.domain.user.dto.CreateUserRequest;
import com.eardream.domain.user.dto.UpdateUserRequest;
import com.eardream.domain.user.dto.UserDto;
import com.eardream.domain.user.entity.UserType;
import com.eardream.domain.user.service.UserService;
import com.eardream.global.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * User 도메인 REST API Controller
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 사용자 생성
     * POST /api/v1/users
     */
    @PostMapping
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
     * ID로 사용자 조회
     * GET /api/v1/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        try {
            UserDto user = userService.getUserById(id);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_FETCH_FAILED", "사용자 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * Clerk ID로 사용자 조회
     * GET /api/v1/users/clerk/{clerkId}
     */
    @GetMapping("/clerk/{clerkId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByClerkId(@PathVariable String clerkId) {
        try {
            UserDto user = userService.getUserByClerkId(clerkId);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_FETCH_FAILED", "사용자 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 전화번호로 사용자 조회
     * GET /api/v1/users/phone/{phoneNumber}
     */
    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<ApiResponse<UserDto>> getUserByPhoneNumber(@PathVariable String phoneNumber) {
        try {
            UserDto user = userService.getUserByPhoneNumber(phoneNumber);
            return ResponseEntity.ok(ApiResponse.success(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_FETCH_FAILED", "사용자 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 유형별 목록 조회
     * GET /api/v1/users?type={userType}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDto>>> getUsersByType(
            @RequestParam(required = false) UserType type,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<UserDto> users;
            
            if (type != null) {
                users = userService.getUsersByType(type);
            } else {
                users = userService.getRecentUsers(limit);
            }
            
            return ResponseEntity.ok(ApiResponse.success(users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_LIST_FAILED", "사용자 목록 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 가족 리더 목록 조회
     * GET /api/v1/users/leaders
     */
    @GetMapping("/leaders")
    public ResponseEntity<ApiResponse<List<UserDto>>> getFamilyLeaders() {
        try {
            List<UserDto> leaders = userService.getFamilyLeaders();
            return ResponseEntity.ok(ApiResponse.success(leaders));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("LEADERS_FETCH_FAILED", "리더 목록 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 소식지 수신자 목록 조회
     * GET /api/v1/users/receivers
     */
    @GetMapping("/receivers")
    public ResponseEntity<ApiResponse<List<UserDto>>> getNewsletterReceivers() {
        try {
            List<UserDto> receivers = userService.getNewsletterReceivers();
            return ResponseEntity.ok(ApiResponse.success(receivers));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RECEIVERS_FETCH_FAILED", "수신자 목록 조회 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 정보 수정
     * PUT /api/v1/users/{id}
     */
    @PutMapping("/{id}")
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
     * 사용자 리더 권한 변경
     * PATCH /api/v1/users/{id}/leader
     */
    @PatchMapping("/{id}/leader")
    public ResponseEntity<ApiResponse<Void>> updateLeaderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean isLeader = request.get("isLeader");
            if (isLeader == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_REQUEST", "isLeader 필드가 필요합니다"));
            }
            
            userService.updateLeaderStatus(id, isLeader);
            return ResponseEntity.ok(ApiResponse.success(null, "리더 권한이 성공적으로 변경되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("LEADER_UPDATE_FAILED", "리더 권한 변경 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 수신자 상태 변경
     * PATCH /api/v1/users/{id}/receiver
     */
    @PatchMapping("/{id}/receiver")
    public ResponseEntity<ApiResponse<Void>> updateReceiverStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean isReceiver = request.get("isReceiver");
            if (isReceiver == null) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("INVALID_REQUEST", "isReceiver 필드가 필요합니다"));
            }
            
            userService.updateReceiverStatus(id, isReceiver);
            return ResponseEntity.ok(ApiResponse.success(null, "수신자 상태가 성공적으로 변경되었습니다"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("USER_NOT_FOUND", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("RECEIVER_UPDATE_FAILED", "수신자 상태 변경 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 삭제
     * DELETE /api/v1/users/{id}
     */
    @DeleteMapping("/{id}")
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
    
    /**
     * 사용자 존재 여부 확인
     * GET /api/v1/users/exists?clerkId={clerkId}&phoneNumber={phoneNumber}
     */
    @GetMapping("/exists")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkUserExists(
            @RequestParam(required = false) String clerkId,
            @RequestParam(required = false) String phoneNumber) {
        try {
            Map<String, Boolean> result = new java.util.HashMap<>();
            
            if (clerkId != null) {
                result.put("clerkIdExists", userService.existsByClerkId(clerkId));
            }
            
            if (phoneNumber != null) {
                result.put("phoneNumberExists", userService.existsByPhoneNumber(phoneNumber));
            }
            
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("USER_EXISTS_CHECK_FAILED", "사용자 존재 확인 중 오류가 발생했습니다"));
        }
    }
    
    /**
     * 사용자 통계 조회
     * GET /api/v1/users/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUserStats() {
        try {
            Map<String, Integer> stats = new java.util.HashMap<>();
            stats.put("totalUsers", userService.getTotalUserCount());
            stats.put("activeUsers", userService.getUserCountByType(UserType.ACTIVE_USER));
            stats.put("pendingRecipients", userService.getUserCountByType(UserType.PENDING_RECIPIENT));
            
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("STATS_FETCH_FAILED", "사용자 통계 조회 중 오류가 발생했습니다"));
        }
    }
}