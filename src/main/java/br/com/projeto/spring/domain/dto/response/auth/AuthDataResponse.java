package br.com.projeto.spring.domain.dto.response.auth;

public record AuthDataResponse(

        String accessToken,

        String tipo,

        AuthUsuarioResponse usuario

) {}
