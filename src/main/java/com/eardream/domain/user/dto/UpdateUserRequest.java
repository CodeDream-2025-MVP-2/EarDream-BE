package com.eardream.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 사용자 정보 수정 요청 DTO
 */
public class UpdateUserRequest {
    
    @Size(max = 100, message = "이름은 100자를 초과할 수 없습니다")
    private String name;
    
    @Pattern(regexp = "^01[0-9]-?[0-9]{4}-?[0-9]{4}$", message = "올바른 휴대폰 번호 형식이 아닙니다")
    private String phoneNumber;
    
    private String profileImageUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    
    @Size(max = 500, message = "주소는 500자를 초과할 수 없습니다")
    private String address;
    
    @Size(max = 50, message = "가족 역할은 50자를 초과할 수 없습니다")
    private String familyRole;
    
    private Boolean isReceiver;
    
    // 기본 생성자
    public UpdateUserRequest() {}
    
    // 변경사항이 있는지 확인하는 메서드
    public boolean hasChanges() {
        return name != null || phoneNumber != null || profileImageUrl != null || 
               birthDate != null || address != null || familyRole != null || isReceiver != null;
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
}