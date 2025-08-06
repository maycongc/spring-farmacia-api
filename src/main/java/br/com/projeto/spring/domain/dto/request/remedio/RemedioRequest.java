package br.com.projeto.spring.domain.dto.request.remedio;

import java.time.LocalDate;

import br.com.projeto.spring.domain.dto.request.EntidadeIdRequest;
import br.com.projeto.spring.domain.model.Via;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record RemedioRequest(

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String nome,

        @Enumerated(EnumType.STRING)
        Via via,

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String lote,

        @PositiveOrZero(message = ValidationMessagesKeys.REMEDIO_QUANTIDADE_POSITIVA_OU_ZERO)
        @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        Integer quantidade,

        @Future(message = ValidationMessagesKeys.REMEDIO_VALIDADE_FUTURA)
        LocalDate validade,

        @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        @Valid
        EntidadeIdRequest laboratorio) {

}
