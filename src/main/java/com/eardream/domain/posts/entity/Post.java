package com.eardream.domain.posts.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

	private Long id;
	private Long familyId;
	private Long userId;
	private String title;
	private String content;
	private String postMonth; // YYYY-MM
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}



