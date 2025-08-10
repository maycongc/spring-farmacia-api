package br.com.projeto.spring.domain.dto.request;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EntidadeIdRequest(

        @NotNull
        @Positive(message = ValidationMessagesKeys.GENERICO_POSITIVO)
        Long id) {

}
