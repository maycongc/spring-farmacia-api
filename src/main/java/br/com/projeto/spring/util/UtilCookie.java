package br.com.projeto.spring.util;

import java.time.Duration;

import org.springframework.http.ResponseCookie;

public class UtilCookie {

    private static ResponseCookie gerarCookieRefreshToken(String refreshToken, long validade) {

        return ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
                .secure(System.getenv("NODE_ENV").equals("production")).path("/").maxAge(validade)
                .sameSite(System.getenv("NODE_ENV").equals("production") ? "None" : "Lax").build();
    }

    public static ResponseCookie gerarCookieLogin(String refreshToken, Boolean rememberMe) {
        long ttlSeconds = rememberMe ? Duration.ofDays(15).getSeconds() : Duration.ofHours(12).getSeconds();

        return gerarCookieRefreshToken(refreshToken, ttlSeconds);
    }

    public static ResponseCookie gerarCookieLogout(String refreshToken) {
        return gerarCookieRefreshToken(refreshToken, 0);
    }
}
