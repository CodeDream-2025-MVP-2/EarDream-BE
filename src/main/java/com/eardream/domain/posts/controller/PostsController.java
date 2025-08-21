package com.eardream.domain.posts.controller;

import com.eardream.domain.posts.dto.PostDto;
import com.eardream.domain.posts.dto.PostImageDto;
import com.eardream.domain.posts.service.PostService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "그룹 소식 API")
public class PostsController {

	private final PostService postService;

	@GetMapping("/familes/{id}/posts")
	@Operation(summary = "그룹 소식 목록", description = "가족 그룹의 소식 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<PostDto>>> getPosts(@PathVariable("id") Long familyId) {
		List<PostDto> posts = postService.getPostsByFamily(familyId);
		return ResponseEntity.ok(ApiResponse.success(posts));
	}

	@PostMapping("/familes/{id}/posts")
	@Operation(summary = "소식 작성", description = "제목/내용과 이미지 목록으로 소식을 작성합니다.")
	public ResponseEntity<ApiResponse<PostDto>> createPost(
			@PathVariable("id") Long familyId,
			@RequestParam("userId") Long userId,
			@RequestParam("title") String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestBody(required = false) List<PostImageDto> images
	) {
		PostDto created = postService.createPost(familyId, userId, title, content, images);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "소식이 생성되었습니다"));
	}

	@GetMapping("/posts/{id}")
	@Operation(summary = "소식 상세", description = "소식 ID로 상세 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<PostDto>> getPost(@PathVariable("id") Long id) {
		PostDto post = postService.getPost(id);
		return ResponseEntity.ok(ApiResponse.success(post));
	}
}



