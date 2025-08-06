package br.com.projeto.spring.domain.dto.response;

public record TokenResponse(String accessToken, String refreshToken, String tipo) {}
