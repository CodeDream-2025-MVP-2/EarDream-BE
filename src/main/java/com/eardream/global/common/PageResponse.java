package com.eardream.global.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 페이징 응답 공통 DTO
 */
@Getter
@Setter
public class PageResponse<T> {
    
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    
    public PageResponse() {}
    
    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / size);
        this.first = page == 1;
        this.last = page >= totalPages;
    }
    
    // Static factory method
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements) {
        return new PageResponse<>(content, page, size, totalElements);
    }
}