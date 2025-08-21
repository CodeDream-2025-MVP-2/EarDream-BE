package com.eardream.domain.familes.mapper;

import com.eardream.domain.familes.entity.Family;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FamilyMapper {

	int insertFamily(Family family);

	Optional<Family> findById(@Param("id") Long id);

	Optional<Family> findByUserId(@Param("userId") Long userId);

	Optional<Family> findByInviteCode(@Param("inviteCode") String inviteCode);

	List<Family> findAll(@Param("offset") int offset, @Param("limit") int limit);

	int countAll();
}



