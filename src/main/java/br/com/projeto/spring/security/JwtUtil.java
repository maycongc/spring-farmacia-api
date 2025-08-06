package br.com.projeto.spring.security;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final Key jwtSecret;
    private final long jwtAccessTokenExpirationMs;
    private final long jwtRefreshTokenExpirationMs;

    public JwtUtil(

            @Value("${jwt.secret}")
            String secret,

            @Value("${jwt.expiration}")
            long expirationMs,

            @Value("${jwt.refresh.expiration}")
            long refreshExpirationMs) {

        this.jwtSecret = Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
        this.jwtAccessTokenExpirationMs = expirationMs;
        this.jwtRefreshTokenExpirationMs = refreshExpirationMs;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenExpirationMs);

        return Jwts.builder().subject(username).issuedAt(now).expiration(expiryDate).signWith(jwtSecret).compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (jwtRefreshTokenExpirationMs));

        return Jwts.builder().setSubject(username).setIssuedAt(now).setExpiration(expiryDate).claim("type", "refresh")
                .signWith(jwtSecret, SignatureAlgorithm.HS256).compact();
    }

    public boolean isRefreshToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody();
        return "refresh".equals(claims.get("type"));
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
