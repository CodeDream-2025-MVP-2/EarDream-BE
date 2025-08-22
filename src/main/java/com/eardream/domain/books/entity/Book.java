package com.eardream.domain.books.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Book {
	private Long id;
	private Long familyId;
	private String name;
	private String pdfUrl;
	private String imageUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}




