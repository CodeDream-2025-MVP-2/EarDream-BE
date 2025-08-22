package com.eardream.domain.books.service;

import com.eardream.domain.books.dto.BookDto;
import com.eardream.domain.books.dto.CreateBookRequest;
import com.eardream.domain.books.dto.RenameBookRequest;
import com.eardream.domain.books.entity.Book;
import com.eardream.domain.books.mapper.BookMapper;
import com.eardream.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

	private final BookMapper bookMapper;

	public List<BookDto> getBooks(Long familyId) {
		return bookMapper.findByFamilyId(familyId).stream().map(this::toDto).collect(Collectors.toList());
	}

	public BookDto getBook(Long id) {
		Book book = bookMapper.findById(id).orElseThrow(() -> new ResourceNotFoundException("책자를 찾을 수 없습니다: " + id));
		return toDto(book);
	}

	@Transactional
	public BookDto createBook(CreateBookRequest req) {
		Book book = Book.builder()
				.familyId(req.getFamilyId())
				.name(req.getName())
				.pdfUrl(req.getPdfUrl())
				.imageUrl(req.getImageUrl())
				.build();
		int result = bookMapper.insertBook(book);
		if (result == 0) throw new IllegalStateException("책자 생성 실패");
		return toDto(book);
	}

	@Transactional
	public void renameBook(RenameBookRequest req) {
		int result = bookMapper.renameBook(req.getBookId(), req.getName());
		if (result == 0) throw new ResourceNotFoundException("책자를 찾을 수 없습니다: " + req.getBookId());
	}

	@Transactional
	public void updateBookFiles(Long bookId, String pdfUrl, String imageUrl) {
		Book book = bookMapper.findById(bookId)
				.orElseThrow(() -> new ResourceNotFoundException("책자를 찾을 수 없습니다: " + bookId));
		bookMapper.updateBookFiles(bookId, pdfUrl, imageUrl);
	}

	private BookDto toDto(Book b) {
		return BookDto.builder()
				.id(b.getId())
				.familyId(b.getFamilyId())
				.name(b.getName())
				.pdfUrl(b.getPdfUrl())
				.imageUrl(b.getImageUrl())
				.createdAt(b.getCreatedAt())
				.updatedAt(b.getUpdatedAt())
				.build();
	}
}


