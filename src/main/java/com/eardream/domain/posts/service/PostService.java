package com.eardream.domain.posts.service;

import com.eardream.domain.posts.dto.PostDto;
import com.eardream.domain.posts.dto.PostImageDto;
import com.eardream.domain.posts.entity.Post;
import com.eardream.domain.posts.entity.PostImage;
import com.eardream.domain.posts.mapper.PostMapper;
import com.eardream.global.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

	private final PostMapper postMapper;

	@Transactional
	public PostDto createPost(Long familyId, Long userId, String title, String content, List<PostImageDto> images) {
		Post post = new Post();
		post.setFamilyId(familyId);
		post.setUserId(userId);
		post.setTitle(title);
		post.setContent(content);
		post.setPostMonth(DateUtils.getCurrentMonthString());
		post.setCreatedAt(LocalDateTime.now());
		post.setUpdatedAt(LocalDateTime.now());

		int result = postMapper.insertPost(post);
		if (result == 0) {
			throw new RuntimeException("소식 생성에 실패했습니다");
		}

		if (images != null) {
			for (PostImageDto imageDto : images) {
				PostImage img = new PostImage();
				img.setPostId(post.getId());
				img.setImageUrl(imageDto.getImageUrl());
				img.setImageOrder(imageDto.getImageOrder());
				img.setCreatedAt(LocalDateTime.now());
				postMapper.insertPostImage(img);
			}
		}

		return getPost(post.getId());
	}

	public List<PostDto> getPostsByFamily(Long familyId) {
		List<Post> posts = postMapper.findPostsByFamilyId(familyId);
		return posts.stream().map(this::toDtoWithImages).collect(Collectors.toList());
	}

	public PostDto getPost(Long id) {
		Post post = postMapper.findPostById(id)
				.orElseThrow(() -> new IllegalArgumentException("소식을 찾을 수 없습니다: " + id));
		return toDtoWithImages(post);
	}

	@Transactional
	public PostDto updatePost(Long id, String title, String content) {
		int updated = postMapper.updatePost(id, title, content);
		if (updated == 0) {
			throw new IllegalArgumentException("소식을 찾을 수 없습니다: " + id);
		}
		return getPost(id);
	}

	@Transactional
	public PostDto updatePostWithImages(Long id, String title, String content, java.util.List<com.eardream.domain.posts.dto.PostImageDto> images) {
		int updated = postMapper.updatePost(id, title, content);
		if (updated == 0) {
			throw new IllegalArgumentException("소식을 찾을 수 없습니다: " + id);
		}
		// 이미지 전체 교체
		postMapper.deleteImagesByPostId(id);
		if (images != null) {
			for (com.eardream.domain.posts.dto.PostImageDto imgDto : images) {
				com.eardream.domain.posts.entity.PostImage img = new com.eardream.domain.posts.entity.PostImage();
				img.setPostId(id);
				img.setImageUrl(imgDto.getImageUrl());
				img.setImageOrder(imgDto.getImageOrder());
				img.setCreatedAt(java.time.LocalDateTime.now());
				postMapper.insertPostImage(img);
			}
		}
		return getPost(id);
	}

	@Transactional
	public void deletePost(Long id) {
		int deleted = postMapper.deletePost(id);
		if (deleted == 0) {
			throw new IllegalArgumentException("소식을 찾을 수 없습니다: " + id);
		}
	}

	private PostDto toDtoWithImages(Post post) {
		List<PostImage> images = postMapper.findImagesByPostId(post.getId());
		List<PostImageDto> imageDtos = images.stream().map(img -> PostImageDto.builder()
				.id(img.getId())
				.imageUrl(img.getImageUrl())
				.imageOrder(img.getImageOrder())
				.build()).collect(Collectors.toList());

		return PostDto.builder()
				.id(post.getId())
				.familyId(post.getFamilyId())
				.userId(post.getUserId())
				.title(post.getTitle())
				.content(post.getContent())
				.postMonth(post.getPostMonth())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.images(imageDtos)
				.build();
	}
}







