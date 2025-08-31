package br.com.projeto.spring.domain.dto.request.auth;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String nome,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String username,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String senha,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        @Email(message = ValidationMessagesKeys.GENERICO_EMAIL_INVALIDO)
        String email,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        @Pattern(regexp = "^\\d{11}$", message = ValidationMessagesKeys.GENERICO_CPF_INVALIDO)
        String cpf,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String dataNascimento

) {}
