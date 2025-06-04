package com.feastora.food_ordering.Utility;

import com.feastora.food_ordering.model.UserModel;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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

    private Claims getAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Object extractValueByKey(String token, String key) {
        return getAllClaims(token).get(key);
    }

    public Date extractExpiration(String token) {
        return getAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateTokenForUserModel(UserModel userModel) {
        return buildToken(Jwts.builder()
                .claim("userId", userModel.getUserId())
                .claim("userName", userModel.getUserName())
                .claim("userModel", userModel), TOKEN_EXPIRY_MILLIS);
    }

    public UserModel getUserModelFromToken(String token) {
        try {
           Claims claims = getAllClaims(token);
           return claims.get("userModel", UserModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    public String generateQRToken(String userId, long tableNumber) {
        return buildToken(Jwts.builder()
                .claim("userId", userId)
                .claim("tableNumber", tableNumber), QR_TOKEN_EXPIRY_MILLIS);
    }

    public Claims validateToken(String token) {
        try {
            return getAllClaims(token);
        } catch (JwtException e) {
            return null;
        }
    }

    public String getUserId(String token) {
        return getAllClaims(token).get("userId", String.class);
    }

    public Long getTableNumber(String token) {
        return getAllClaims(token).get("tableNumber", Long.class);
    }

    public String generateSessionToken(String userId, long tableNumber, String ip, String userAgent) {
        return buildToken(Jwts.builder()
                .claim("userId", userId)
                .claim("ip", ip)
                .claim("userAgent", userAgent)
                .claim("tableNumber", tableNumber), SESSION_EXPIRY_MILLIS);
    }

    public boolean validateSessionToken(String token, String ip, String userAgent) {
        try {
            Claims claims = getAllClaims(token);
            return ip.equals(claims.get("ip", String.class)) &&
                    userAgent.equals(claims.get("userAgent", String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    private String buildToken(JwtBuilder builder, long expiryMillis) {
        return builder
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiryMillis))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }
}
