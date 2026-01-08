package com.example.clubmanagement.security;


import com.example.clubmanagement.entity.User;
import com.example.clubmanagement.mapper.UserMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Autowired
    private UserMapper userMapper;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationInMs;

    /**
     * 生成 JWT Token
     */
    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 将 jwtSecret 转换为 Key 对象
        Key key = getSigningKey();

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS512) // 使用 Key 对象
                .compact();
    }

    // 确保 jwtSecret 的长度足够
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 64) {
            keyBytes = Arrays.copyOf(keyBytes, 64); // 扩展为 32 字节
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 从 JWT Token 中获取用户 ID
     */

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 验证 JWT Token 是否有效
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            // 无效的JWT签名
        } catch (MalformedJwtException ex) {
            // 无效的JWT token
        } catch (ExpiredJwtException ex) {
            // 过期的JWT token
        } catch (UnsupportedJwtException ex) {
            // 不支持的JWT token
        } catch (IllegalArgumentException ex) {
            // JWT claims字符串为空
        }
        return false;
    }

    /**
     * 根据用户 ID 创建 Authentication 对象
     */
    public Authentication getAuthentication(Long userId) {

        // 从数据库加载用户信息
        User user = userMapper.findByUserId(userId);

        // 创建 UserPrincipal 对象
        UserPrincipal userPrincipal = UserPrincipal.create(user);

        // 将角色转换为 GrantedAuthority
        Collection<? extends GrantedAuthority> authorities = userPrincipal.getAuthorities();

        // 返回 UsernamePasswordAuthenticationToken
        return new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);

    }
}