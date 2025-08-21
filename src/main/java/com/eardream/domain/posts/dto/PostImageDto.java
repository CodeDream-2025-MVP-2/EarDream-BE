package com.eardream.domain.posts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostImageDto {
	private Long id;
	private String imageUrl;
	private String description;
	private Integer imageOrder;
}



