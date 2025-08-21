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
public class PostImage {

	private Long id;
	private Long postId;
	private String imageUrl;
	private String description;
	private Integer imageOrder;
	private LocalDateTime createdAt;
}



