package com.eardream.domain.posts.controller;

import com.eardream.domain.posts.dto.PostDto;
import com.eardream.domain.posts.dto.PostImageDto;
import com.eardream.domain.posts.dto.UpdatePostRequest;
import com.eardream.domain.posts.service.PostService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.eardream.global.util.FileUtils;

import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Posts", description = "그룹 소식 API")
public class PostsController {

	private final PostService postService;

	@Value("${file.upload.path}")
	private String uploadPath;

	@GetMapping("/familes/{id}/posts")
	@Operation(summary = "그룹 소식 목록", description = "가족 그룹의 소식 목록을 조회합니다.")
	public ResponseEntity<ApiResponse<List<PostDto>>> getPosts(@PathVariable("id") Long familyId) {
		List<PostDto> posts = postService.getPostsByFamily(familyId);
		return ResponseEntity.ok(ApiResponse.success(posts));
	}

	@PostMapping(value = "/familes/{id}/posts", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "소식 작성", description = "제목/내용과 여러 이미지로 소식을 작성합니다.")
	public ResponseEntity<ApiResponse<PostDto>> createPost(
			@PathVariable("id") Long familyId,
			@RequestParam("userId") Long userId,
			@RequestParam("title") String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestPart(name = "images", required = false) List<MultipartFile> imageFiles

	) throws Exception {
		List<PostImageDto> images = null;
		if (imageFiles != null && !imageFiles.isEmpty()) {
			images = new ArrayList<>();
			for (int i = 0; i < imageFiles.size(); i++) {
				MultipartFile file = imageFiles.get(i);
				if (file.isEmpty()) continue;
				if (!FileUtils.isValidFileSize(file)) {
					return ResponseEntity.badRequest().body(ApiResponse.error("FILE_TOO_LARGE", "이미지 파일이 너무 큽니다"));
				}
				if (!FileUtils.isImageFile(file.getOriginalFilename())) {
					return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_TYPE", "이미지 파일만 허용됩니다"));
				}
				String relativePath = FileUtils.saveFile(file, uploadPath, "images");
				String urlPath = "/uploads/" + StringUtils.trimLeadingCharacter(relativePath.replace("\\", "/"), '/');
				images.add(PostImageDto.builder()
						.imageUrl(urlPath)
						.imageOrder(i + 1)
						.build());
			}
		}

		PostDto created = postService.createPost(familyId, userId, title, content, images);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(created, "소식이 생성되었습니다"));
	}

	@GetMapping("/posts/{id}")
	@Operation(summary = "소식 상세", description = "소식 ID로 상세 정보를 조회합니다.")
	public ResponseEntity<ApiResponse<PostDto>> getPost(@PathVariable("id") Long id) {
		PostDto post = postService.getPost(id);
		return ResponseEntity.ok(ApiResponse.success(post));
	}

	@PatchMapping(value = "/posts/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "소식 수정", description = "제목/내용 및 이미지를 multipart로 수정(이미지 전체 교체)")
	public ResponseEntity<ApiResponse<PostDto>> updatePost(
			@PathVariable("id") Long id,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "content", required = false) String content,
			@RequestPart(value = "images", required = false) java.util.List<MultipartFile> imageFiles
	) throws Exception {
		PostDto updated;
		if (imageFiles != null) {
			java.util.List<PostImageDto> images = new java.util.ArrayList<>();
			for (int i = 0; i < imageFiles.size(); i++) {
				MultipartFile file = imageFiles.get(i);
				if (file == null || file.isEmpty()) continue;
				if (!FileUtils.isValidFileSize(file)) {
					return ResponseEntity.badRequest().body(ApiResponse.error("FILE_TOO_LARGE", "이미지 파일이 너무 큽니다"));
				}
				if (!FileUtils.isImageFile(file.getOriginalFilename())) {
					return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_TYPE", "이미지 파일만 허용됩니다"));
				}
				String relative = FileUtils.saveFile(file, uploadPath, "images");
				String urlPath = "/uploads/" + StringUtils.trimLeadingCharacter(relative.replace("\\", "/"), '/');
				images.add(PostImageDto.builder().imageUrl(urlPath).imageOrder(i + 1).build());
			}
			updated = postService.updatePostWithImages(id, title, content, images);
		} else {
			updated = postService.updatePost(id, title, content);
		}
		return ResponseEntity.ok(ApiResponse.success(updated, "소식이 수정되었습니다"));
	}

	@DeleteMapping("/posts/{id}")
	@Operation(summary = "소식 삭제", description = "소식을 삭제합니다.")
	public ResponseEntity<ApiResponse<Void>> deletePost(@PathVariable("id") Long id) {
		postService.deletePost(id);
		return ResponseEntity.ok(ApiResponse.success(null, "소식이 삭제되었습니다"));
	}
}




