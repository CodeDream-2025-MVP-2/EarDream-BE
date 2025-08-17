package com.eardream.domain.user.dto;

import com.eardream.domain.user.entity.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 사용자 생성 요청 DTO
 */
public class CreateUserRequest {
    
    @NotBlank(message = "이름은 필수입니다")
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    private String name;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String phoneNumber;
    
    private String profileImageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @Size(max = 500, message = "주소는 500자를 초과할 수 없습니다")
    private String address;
    
    @NotNull(message = "사용자 유형은 필수입니다")
    private UserType userType;
    
    @Size(max = 50, message = "가족 역할은 50자를 초과할 수 없습니다")
    private String familyRole;
    
    private Boolean isReceiver = false;
    
    // Clerk ID (활성 사용자인 경우 필수)
    private String clerkId;
    
    // 기본 생성자
    public CreateUserRequest() {}
    
    // 생성자
    public CreateUserRequest(String name, String phoneNumber, UserType userType) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }
    
    // 검증 메서드
    public boolean isValidForActiveUser() {
        return UserType.ACTIVE_USER.equals(userType) && clerkId != null && !clerkId.trim().isEmpty();
    }
    
    public boolean isValidForPendingRecipient() {
        return UserType.PENDING_RECIPIENT.equals(userType) && address != null && !address.trim().isEmpty();
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public UserType getUserType() {
        return userType;
    }
    
    public void setUserType(UserType userType) {
        this.userType = userType;
    }
    
    public String getFamilyRole() {
        return familyRole;
    }
    
    public void setFamilyRole(String familyRole) {
        this.familyRole = familyRole;
    }
    
    public Boolean getIsReceiver() {
        return isReceiver;
    }
    
    public void setIsReceiver(Boolean isReceiver) {
        this.isReceiver = isReceiver;
    }
    
    public String getClerkId() {
        return clerkId;
    }
    
    public void setClerkId(String clerkId) {
        this.clerkId = clerkId;
    }
}