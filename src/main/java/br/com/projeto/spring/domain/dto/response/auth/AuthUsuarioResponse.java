package br.com.projeto.spring.domain.dto.response.auth;

import java.util.List;

public record AuthUsuarioResponse(

        Long id,

        String username,

        String nome,

        String email,

        List<String> permissoes,

        boolean isAdmin

) {

    public AuthUsuarioResponse(

            Long id,

            String username,

            String nome,

            String email,

            List<String> permissoes,

            boolean isAdmin

    ) {

        this.id = id;
        this.username = username;
        this.nome = nome;
        this.email = email;
        this.permissoes = permissoes;
        this.isAdmin = isAdmin;
    }

}
