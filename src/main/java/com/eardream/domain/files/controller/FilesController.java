package com.eardream.domain.files.controller;

import com.eardream.global.common.ApiResponse;
import com.eardream.global.util.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "Files", description = "파일 업로드 API")
public class FilesController {

	@Value("${file.upload.path}")
	private String uploadPath;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "이미지 파일 업로드", description = "이미지 파일을 업로드하고 경로를 반환합니다.")
	public ResponseEntity<ApiResponse<Map<String, String>>> upload(@RequestPart("file") MultipartFile file) throws IOException {
		if (file.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.error("EMPTY_FILE", "파일이 비어있습니다"));
		}
		if (!FileUtils.isValidFileSize(file)) {
			return ResponseEntity.badRequest().body(ApiResponse.error("FILE_TOO_LARGE", "파일 크기가 너무 큽니다"));
		}
		if (!FileUtils.isImageFile(file.getOriginalFilename())) {
			return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_TYPE", "이미지 파일만 업로드 가능합니다"));
		}

		String relativePath = FileUtils.saveFile(file, uploadPath, "images");
		String urlPath = "/uploads/" + StringUtils.trimLeadingCharacter(relativePath.replace("\\", "/"), '/');
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(Map.of("url", urlPath), "업로드 성공"));
	}
}



