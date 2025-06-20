package com.feastora.food_ordering.Utility;

import com.feastora.food_ordering.constants.Constants;
import com.feastora.food_ordering.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    private SecretKey secretKey;

    private static final long TOKEN_EXPIRY_MILLIS = Duration.ofHours(24).toMillis();
    private static final long SESSION_EXPIRY_MILLIS = Duration.ofHours(2).toMillis();
    private static final long QR_TOKEN_EXPIRY_MILLIS = Duration.ofHours(4).toMillis();

    @PostConstruct
    public void init() {
        if (secretKeyString == null || secretKeyString.length() < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 32 characters");
        }
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    // ======== Common Token Operations ========
    public Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String buildToken(JwtBuilder builder, long expiryMillis) {
        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    public Date extractExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    public Object extractValueByKey(String token, String key) {
        return getAllClaims(token).get(key);
    }

    // ======== User Token ========
    public String generateTokenForUser(User user) {
        return buildToken(Jwts.builder()
                .claim(Constants.USERID, user.getUserId())
                .claim(Constants.USER_ROLE, user.getRole())
                .claim(Constants.USERNAME, user.getUserName()), TOKEN_EXPIRY_MILLIS);
    }

    public boolean validateUserToken(String token, String expectedRole) {
        try {
            if (isTokenExpired(token)) return false;
            Claims claims = getAllClaims(token);
            String role = claims.get(Constants.USER_ROLE, String.class);
            return role != null && role.equalsIgnoreCase(expectedRole);
        } catch (JwtException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return getAllClaims(token).get(Constants.USERID, String.class);
    }

    // ======== QR Token ========
    public String generateQRToken(String userId, long tableNumber) {
        return buildToken(Jwts.builder()
                .claim(Constants.USERID, userId)
                .claim(Constants.USER_ROLE, "CUSTOMER")
                .claim(Constants.TABLE_NUM, tableNumber), QR_TOKEN_EXPIRY_MILLIS);
    }

    public Long getTableNumber(String token) {
        return getAllClaims(token).get(Constants.TABLE_NUM, Long.class);
    }

    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (JwtException e) {
            return false;
        }
    }

    // ======== Session Token ========
    public String generateSessionToken(String userId, long tableNumber, String ip, String userAgent) {
        return buildToken(Jwts.builder()
                .claim(Constants.USERID, userId)
                .claim(Constants.USER_ROLE, "CUSTOMER")
                .claim(Constants.TABLE_NUM, tableNumber)
                .claim("ip", ip)
                .claim("userAgent", userAgent), SESSION_EXPIRY_MILLIS);
    }

    public boolean validateSessionToken(String token, HttpServletRequest request) {
        try {
            Claims claims = getAllClaims(token);
            String ip = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            return !isTokenExpired(token) &&
                    ip.equals(claims.get("ip", String.class)) &&
                    userAgent.equals(claims.get("userAgent", String.class));
        } catch (JwtException e) {
            return false;
        }
    }
}