package com.eardream.domain.families.mapper;

import com.eardream.domain.families.entity.Family;
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

	List<com.eardream.domain.families.dto.FamilyMemberDto> findMembersByFamilyId(@Param("familyId") Long familyId);

	java.util.List<com.eardream.domain.families.dto.InvitationReviewDto> findPendingInvitations(@Param("familyId") Long familyId);

	int approveInvitation(@Param("invitationId") Long invitationId);

	int rejectInvitation(@Param("invitationId") Long invitationId);

	int insertInvitation(com.eardream.domain.families.entity.Invitation invitation);

	int insertFamilyMember(@Param("familyId") Long familyId,
	                      @Param("userId") Long userId,
	                      @Param("relationship") String relationship,
	                      @Param("role") String role);

	int updateFamilyInviteCode(@Param("familyId") Long familyId, @Param("inviteCode") String inviteCode);

	Optional<com.eardream.domain.families.entity.Invitation> findInvitationById(@Param("invitationId") Long invitationId);

	int deleteFamilyMember(@Param("familyId") Long familyId, @Param("userId") Long userId);
}



