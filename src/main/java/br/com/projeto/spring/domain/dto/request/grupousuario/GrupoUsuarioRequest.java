package br.com.projeto.spring.domain.dto.request.grupousuario;

import java.util.List;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GrupoUsuarioRequest(

        @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        String nome,

        String descricao,

        @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
        List<Long> permissoes

) {}
