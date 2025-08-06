package br.com.projeto.spring.util;

import org.springframework.http.ResponseCookie;

public class UtilCookie {

    public static ResponseCookie gerarCookieRefreshToken(String refreshToken) {
        int validade = 7 * 24 * 60 * 60; // 7 dias em segundos

        return ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(true).path("/api/auth")
                .maxAge(validade).sameSite("Strict").build();
    }
}
