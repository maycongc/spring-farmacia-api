package br.com.projeto.spring.domain.dto.request.usuario;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.validation.constraints.Pattern;

public record UsuarioUpdateRequest(

        String username,

        String nome,

        String email,

        String dataNascimento,

        String telefone,

        String cep,

        String endereco,

        String complemento,

        String cidade,

        @Pattern(regexp = "^[A-Z]{2}$", message = ValidationMessagesKeys.GENERICO_UF_INVALIDO)
        String uf,

        boolean isAdmin

) {

}
