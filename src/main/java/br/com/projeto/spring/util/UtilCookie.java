package br.com.projeto.spring.util;

import org.springframework.http.ResponseCookie;

public class UtilCookie {

    private static ResponseCookie gerarCookieRefreshToken(String refreshToken, int validade) {

        return ResponseCookie.from("refreshToken", refreshToken).httpOnly(true)
                .secure(System.getenv("NODE_ENV").equals("production")).path("/").maxAge(validade)
                .sameSite(System.getenv("NODE_ENV").equals("production") ? "None" : "Lax").build();
    }

    public static ResponseCookie gerarCookieLogin(String refreshToken, Boolean rememberMe) {
        int validade1Dia = 1 * 24 * 60 * 60; // 1 dia em segundos
        int validade30Dias = 60 * 60 * 24 * 30; // 30 dias em segundos

        int validade = (Util.preenchido(rememberMe) && rememberMe) ? validade30Dias : validade1Dia;

        return gerarCookieRefreshToken(refreshToken, validade);
    }

    public static ResponseCookie gerarCookieRefresh(String refreshToken) {
        int validade = 60 * 60 * 24 * 7; // 7 dias em segundos
        return gerarCookieRefreshToken(refreshToken, validade);
    }

    public static ResponseCookie gerarCookieLogout(String refreshToken) {
        int validade = 0;
        return gerarCookieRefreshToken(refreshToken, validade);
    }
}
