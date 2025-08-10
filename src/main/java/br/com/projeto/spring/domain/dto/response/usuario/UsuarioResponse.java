package br.com.projeto.spring.domain.dto.response.usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(

        Long id,

        String username,

        String nome,

        String email,

        String cpf,

        String dataNascimento,

        String telefone,

        String cep,

        String endereco,

        String complemento,

        String cidade,

        String uf,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
