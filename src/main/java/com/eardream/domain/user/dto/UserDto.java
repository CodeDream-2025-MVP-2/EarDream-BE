package com.eardream.domain.user.dto;

import com.eardream.domain.user.entity.UserType;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 사용자 정보 응답 DTO
 */
public class UserDto {
    
    private Long userId;
    private String clerkId;
    private String name;
    private String phoneNumber;
    private String profileImageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    private String address;
    private UserType userType;
    private String familyRole;
    private Boolean isLeader;
    private Boolean isReceiver;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 기본 생성자
    public UserDto() {}
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getClerkId() {
        return clerkId;
    }
    
    public void setClerkId(String clerkId) {
        this.clerkId = clerkId;
    }
    
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
    
    public Boolean getIsLeader() {
        return isLeader;
    }
    
    public void setIsLeader(Boolean isLeader) {
        this.isLeader = isLeader;
    }
    
    public Boolean getIsReceiver() {
        return isReceiver;
    }
    
    public void setIsReceiver(Boolean isReceiver) {
        this.isReceiver = isReceiver;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}