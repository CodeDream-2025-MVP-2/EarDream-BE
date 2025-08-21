package com.eardream.domain.posts.mapper;

import com.eardream.domain.posts.entity.Post;
import com.eardream.domain.posts.entity.PostImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PostMapper {

	int insertPost(Post post);

	int insertPostImage(PostImage image);

	Optional<Post> findPostById(@Param("id") Long id);

	List<Post> findPostsByFamilyId(@Param("familyId") Long familyId);

	List<PostImage> findImagesByPostId(@Param("postId") Long postId);
}




