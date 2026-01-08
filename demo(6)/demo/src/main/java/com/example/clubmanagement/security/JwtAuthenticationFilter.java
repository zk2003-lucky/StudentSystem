package com.example.clubmanagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenProvider tokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        logger.debug("JWT Filter processing request: " + request.getRequestURI());
        // 1. 从请求头获取JWT
        String jwt = getJwtFromRequest(request);

        // 2. 验证JWT是否有效
        if (jwt != null && tokenProvider.validateToken(jwt)) {
            // 3. 从JWT中提取用户ID
            Long userId = tokenProvider.getUserIdFromToken(jwt);

            // 4. 根据用户ID获取认证信息
            Authentication authentication = tokenProvider.getAuthentication(userId);

            // 5. 将认证信息存入SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 继续执行后续过滤器
        filterChain.doFilter(request, response);
    }

    // 从请求头提取JWT
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // 去掉"Bearer "前缀
        }
        return null;
    }
}