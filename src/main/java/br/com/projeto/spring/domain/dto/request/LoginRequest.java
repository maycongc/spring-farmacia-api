package br.com.projeto.spring.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        String username,

        @NotBlank
        String senha) {

}
