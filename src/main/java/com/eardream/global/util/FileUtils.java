package com.eardream.global.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

/**
 * 파일 관련 유틸리티 클래스
 */
public class FileUtils {
    
    // 허용된 이미지 확장자
    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "webp"
    );
    
    // 허용된 문서 확장자
    private static final List<String> ALLOWED_DOCUMENT_EXTENSIONS = Arrays.asList(
        "pdf"
    );
    
    // 최대 파일 크기 (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    private FileUtils() {
        // 인스턴스 생성 방지
    }
    
    /**
     * 이미지 파일 여부 확인
     */
    public static boolean isImageFile(String fileName) {
        String extension = StringUtils.getFileExtension(fileName);
        return ALLOWED_IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * PDF 파일 여부 확인
     */
    public static boolean isPdfFile(String fileName) {
        String extension = StringUtils.getFileExtension(fileName);
        return ALLOWED_DOCUMENT_EXTENSIONS.contains(extension.toLowerCase());
    }
    
    /**
     * 파일 크기 검증
     */
    public static boolean isValidFileSize(MultipartFile file) {
        return file.getSize() <= MAX_FILE_SIZE;
    }
    
    /**
     * 파일 저장
     */
    public static String saveFile(MultipartFile file, String uploadDir, String subDir) throws IOException {
        // 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir, subDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 파일명 생성 (타임스탬프 + 원본파일명)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String originalFileName = StringUtils.sanitizeFileName(file.getOriginalFilename());
        String fileName = timestamp + "_" + originalFileName;
        
        // 파일 저장
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // 상대 경로 반환
        return Paths.get(subDir, fileName).toString().replace("\\", "/");
    }
    
    /**
     * 파일 삭제
     */
    public static boolean deleteFile(String uploadDir, String filePath) {
        try {
            Path path = Paths.get(uploadDir, filePath);
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public static boolean fileExists(String uploadDir, String filePath) {
        Path path = Paths.get(uploadDir, filePath);
        return Files.exists(path);
    }
    
    /**
     * 파일 크기를 읽기 쉬운 형식으로 변환
     */
    public static String formatFileSize(long size) {
        if (size <= 0) return "0 B";
        
        final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        
        return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }
    
    /**
     * MIME 타입 추출
     */
    public static String getMimeType(String fileName) {
        String extension = StringUtils.getFileExtension(fileName).toLowerCase();
        
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            case "pdf":
                return "application/pdf";
            default:
                return "application/octet-stream";
        }
    }
}