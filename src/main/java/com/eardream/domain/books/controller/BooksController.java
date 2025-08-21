package com.eardream.domain.books.controller;

import com.eardream.domain.books.dto.BookDto;
import com.eardream.domain.books.dto.CreateBookRequest;
import com.eardream.domain.books.dto.RenameBookRequest;
import com.eardream.domain.books.service.BookService;
import com.eardream.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.eardream.global.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/families")
@RequiredArgsConstructor
@Tag(name = "Books", description = "소식 책자 API")
public class BooksController {

	private final BookService bookService;

	@Value("${file.upload.path}")
	private String uploadPath;

	@GetMapping("/books")
	@Operation(summary = "소식 책자 목록", description = "가족의 모든 책자를 조회합니다.")
	public ResponseEntity<ApiResponse<List<BookDto>>> list(@RequestParam("familyId") Long familyId) {
		var list = bookService.getBooks(familyId);
		return ResponseEntity.ok(ApiResponse.success(list));
	}

	@GetMapping("/book")
	@Operation(summary = "소식 책자 단건", description = "책자 ID로 상세 조회합니다.")
	public ResponseEntity<ApiResponse<BookDto>> get(@RequestParam("id") Long id) {
		var dto = bookService.getBook(id);
		return ResponseEntity.ok(ApiResponse.success(dto));
	}

	@PostMapping(value = "/book", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "소식 책자 생성", description = "multipart로 책자 생성 및 파일(PDF/이미지) 업로드")
	public ResponseEntity<ApiResponse<BookDto>> create(
			@RequestParam("familyId") Long familyId,
			@RequestParam("name") String name,
			@RequestPart(value = "file", required = false) MultipartFile file
	) throws Exception {
		if (familyId == null || name == null || name.isBlank()) {
			return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_INPUT", "familyId와 name은 필수입니다"));
		}

		String pdfUrl = null;
		String imageUrl = null;

		try {
			if (file != null && !file.isEmpty()) {
				if (!FileUtils.isValidFileSize(file)) {
					return ResponseEntity.badRequest().body(ApiResponse.error("FILE_SIZE_EXCEEDED", "파일 크기가 너무 큽니다"));
				}

				String original = file.getOriginalFilename();
				if (original != null && FileUtils.isPdfFile(original)) {
					String relative = FileUtils.saveFile(file, uploadPath, "pdfs");
					pdfUrl = "/uploads/" + StringUtils.trimLeadingCharacter(relative.replace("\\", "/"), '/');
				} else if (original != null && FileUtils.isImageFile(original)) {
					String relative = FileUtils.saveFile(file, uploadPath, "images");
					imageUrl = "/uploads/" + StringUtils.trimLeadingCharacter(relative.replace("\\", "/"), '/');
				} else {
					return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_FILE_TYPE", "PDF 또는 이미지 파일만 업로드 가능합니다"));
				}
			}

			var dto = bookService.createBook(
					CreateBookRequest.builder()
							.familyId(familyId)
							.name(name)
							.pdfUrl(pdfUrl)
							.imageUrl(imageUrl)
							.build()
			);
			return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(dto, "책자가 생성되었습니다"));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("FILE_UPLOAD_FAILED", "파일 처리 중 오류가 발생했습니다: " + e.getMessage()));
		}
	}

	@PatchMapping("/book")
	@Operation(summary = "소식 책자 이름 변경", description = "책자 이름을 변경합니다.")
	public ResponseEntity<ApiResponse<Void>> rename(@Valid @RequestBody RenameBookRequest req) {
		bookService.renameBook(req);
		return ResponseEntity.ok(ApiResponse.success(null, "책자 이름이 변경되었습니다"));
	}


}


