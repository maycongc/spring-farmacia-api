package br.com.projeto.spring.domain.dto.request.usuario;

public record UsuarioUpdateRequest(

        String username,

        String senha,

        String nome,

        String email,

        String dataNascimento,

        String telefone,

        String cep,

        String endereco,

        String complemento,

        String cidade,

        String uf,

        boolean isAdmin

) {

}
