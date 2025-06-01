package com.feastora.food_ordering.Utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.feastora.food_ordering.model.UserModel;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashMap;

@Component
public class JwtUtil {

    @Value("${jwt.secret-key}")
    private static String SECRET_KEY;

    private final ObjectMapper objectMapper;

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private SecretKey getSigningKey() {
        if (SECRET_KEY == null) {
            SECRET_KEY = "a2V5c2VjcmV0MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNA==";
        }
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Object extractValueByKey(String token, String key) {
        Claims claims = extractAllClaims(token);
        return claims.get(key, Object.class);
    }
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJwt(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
//    public String generateTokenForUserModel(UserModel userModel) {
//        return Jwts.builder()
//                .claim("userId", userModel.getUserId())
//                .claim("userModel", userModel)
//                .setIssuedAt(new Date())
//                .setExpiration(Date.from(Instant.now().plus(4, HOURS)))
//                .signWith(getSigningKey())     // ← modern API
//                .compact();
//    }

    public String generateTokenForUserModel (UserModel userModel) {
        try {
            return Jwts.builder()
                    .claim("userId", userModel.getUserId())
                    .claim("userModel", userModel)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L))
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public UserModel getUserModelFromToken(String token) {
        Claims claims = validateToken(token);
        if (claims == null) return null;

        LinkedHashMap<?, ?> map = claims.get("userModel", LinkedHashMap.class);
        return objectMapper.convertValue(map, UserModel.class);
    }

    public String generateQRToken(String userId, long tableNumber) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("tableNumber", tableNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .setExpiration(Date.from(Instant.now().plusSeconds(4 * 60 * 60 * 1000L)))
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            System.out.println(e.getMessage());
           return null;
        }
    }

    public String getUserId(String sessionToken) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(sessionToken)
                .getBody()
                .get("userId", String.class);
    }

    public Long getTableNumber(String sessionToken) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .parseClaimsJws(sessionToken)
                .getBody()
                .get("", Long.class);
    }

    public String generateSessionToken(String userId, long tableNumber, String ip, String userAgent) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("ip", ip)
                .claim("userAgent", userAgent)
                .claim("tableNumber", tableNumber)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 2 * 60 * 60 * 1000L)) // 2 hours
                .signWith(SignatureAlgorithm.HS256, getSigningKey())
                .compact();
    }

    public boolean validateSessionToken(String token, String ip, String userAgent) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();

            return ip.equals(claims.get("ip", String.class)) &&
                    userAgent.equals(claims.get("userAgent", String.class));
        } catch (Exception e) {
            return false;
        }
    }
}

