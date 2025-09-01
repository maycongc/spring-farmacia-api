package br.com.projeto.spring.util;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private final SecretKey accessKey;
    private final long ACCESS_TTL_MIN = 15;

    public JwtUtil(

            @Value("${jwt.access.secret}")
            String accessSecretBase64

    ) {

        this.accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecretBase64));
    }

    public String generateAccessToken(String username) {
        Instant now = Instant.now();
        Date expiryDate = Date.from(now.plus(ACCESS_TTL_MIN, ChronoUnit.MINUTES));

        return Jwts.builder().subject(username).issuedAt(Date.from(now)).expiration(expiryDate).claim("type", "access")
                .signWith(accessKey, Jwts.SIG.HS256).compact();
    }

    private Claims parse(String token, SecretKey key) {
        try {
            return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        } catch (JwtException | IllegalArgumentException ex) {
            return null;
        }
    }

    public boolean validateAccessToken(String token) {
        Claims c = parse(token, accessKey);
        return c != null && "access".equals(c.get("type", String.class));
    }

    public String getUsernameFromAccessToken(String token) {
        Claims c = parse(token, accessKey);
        return c == null ? null : c.getSubject();
    }

}
