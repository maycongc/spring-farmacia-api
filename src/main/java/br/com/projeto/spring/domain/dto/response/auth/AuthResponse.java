package br.com.projeto.spring.domain.dto.response.auth;

public record AuthResponse(

        String accessToken,

        String refreshToken,

        String tipo,

        AuthUsuarioResponse usuario

) {}
