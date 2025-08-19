package com.eardream.global.common;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 페이징 요청 공통 DTO
 */
public class PageRequest {
    
    @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
    private int page = 1;
    
    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
    private int size = 20;
    
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
    
    public PageRequest() {}
    
    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
    
    public PageRequest(int page, int size, String sortBy, String sortDirection) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
    
    public int getOffset() {
        return (page - 1) * size;
    }
    
    // Getters and Setters
    public int getPage() {
        return page;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getSize() {
        return size;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public String getSortBy() {
        return sortBy;
    }
    
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    
    public String getSortDirection() {
        return sortDirection;
    }
    
    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }
}