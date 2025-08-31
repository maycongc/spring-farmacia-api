package br.com.projeto.spring.domain.dto.response.auth;

public record RegisterResponse(

        Long id,

        String nome,

        String username,

        String email,

        String cpf,

        String dataNascimento

) {}
