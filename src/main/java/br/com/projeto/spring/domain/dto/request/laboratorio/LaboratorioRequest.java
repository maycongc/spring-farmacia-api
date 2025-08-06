package br.com.projeto.spring.domain.dto.request.laboratorio;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LaboratorioRequest(

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String nome,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String endereco,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?(9?\\d{4})-?\\d{4}$",
                message = ValidationMessagesKeys.GENERICO_TELEFONE_INVALIDO)
        String telefone,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        @Email(message = ValidationMessagesKeys.GENERICO_EMAIL_INVALIDO)
        String email) {

}
