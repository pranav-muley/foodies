package com.feastora.food_ordering.Utility;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private static SecretKey SECRET_KEY;

    public String generateQRToken(String userId, long tableNumber) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("tableNumber", tableNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L))
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
           return null;
        }
    }

    public String getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", String.class);
    }

    public Long getTableNumber(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("tableNumber", Long.class);
    }

    public String generateSessionToken(String userId, long tableNumber, String ip, String userAgent) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("ip", ip)
                .claim("userAgent", userAgent)
                .claim("tableNumber", tableNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L)) // 2 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateSessionToken(String token, String ip, String userAgent) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return ip.equals(claims.get("ip", String.class)) &&
                    userAgent.equals(claims.get("userAgent", String.class));
        } catch (Exception e) {
            return false;
        }
    }
}

