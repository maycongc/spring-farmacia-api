package br.com.projeto.spring.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.util.Util;

@Component
public class AuthUsuarioMapper {

    public AuthUsuarioResponse toResponse(Usuario usuario, List<String> permissoes) {
        if (Util.vazio(usuario)) {
            return null;
        }

        var response = new AuthUsuarioResponse(

                usuario.getId(),

                usuario.getUsername(),

                usuario.getNome(),

                usuario.getEmail(),

                permissoes,

                usuario.isAdmin()

        );

        return response;
    }
}
