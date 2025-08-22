package com.eardream.global.jwt;

import com.eardream.domain.user.service.UserService;
import com.eardream.domain.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 인증 필터
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String token = resolveToken(request);
            
            if (token != null && jwtTokenProvider.validateToken(token) && jwtTokenProvider.isAccessToken(token)) {
                authenticateUser(request, token);
            }
            
        } catch (Exception e) {
            logger.error("JWT 인증 처리 중 오류 발생: " + e.getMessage());
            SecurityContextHolder.clearContext();
            
            // 인증 실패 시 401 응답
            handleAuthenticationError(response, "INVALID_TOKEN", "유효하지 않은 토큰입니다: " + e.getMessage());
            return;
        }
        
        filterChain.doFilter(request, response);
    }
    
    /**
     * 요청에서 JWT 토큰 추출
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        return null;
    }
    
    /**
     * JWT 토큰을 이용해 사용자 인증
     */
    private void authenticateUser(HttpServletRequest request, String token) {
        try {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDto userDto = userService.getUserById(userId);
            
            // Spring Security Authentication 객체 생성
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDto, // Principal
                    null,    // Credentials
                    Arrays.asList(
                        new SimpleGrantedAuthority("ROLE_USER"),
                        userDto.getIsLeader() != null && userDto.getIsLeader() 
                            ? new SimpleGrantedAuthority("ROLE_FAMILY_LEADER") 
                            : new SimpleGrantedAuthority("ROLE_FAMILY_MEMBER")
                    )
            );
            
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
        } catch (Exception e) {
            logger.warn("사용자 인증 실패 - 토큰: " + token + ", 오류: " + e.getMessage());
            throw new RuntimeException("사용자 인증 실패", e);
        }
    }
    
    /**
     * 인증 실패 시 에러 응답 처리
     */
    private void handleAuthenticationError(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("error", Map.of(
            "code", errorCode,
            "message", message
        ));
        errorResponse.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
    
    /**
     * 인증이 필요없는 경로인지 확인
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 인증이 필요없는 경로들
        return path.startsWith("/auth/") ||      // 인증 관련 API
               path.equals("/health") ||                // Health Check
               path.equals("/");        // Root
    }
}