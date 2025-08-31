package br.com.projeto.spring.mapper;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.dto.request.auth.RegisterRequest;
import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.dto.response.auth.RegisterResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.util.Util;

@Component
public class AuthMapper {

    public AuthUsuarioResponse userToResponse(Usuario usuario, List<String> permissoes) {
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

    public RegisterResponse toResponseRegister(Usuario usuario) {
        if (Util.vazio(usuario)) {
            return null;
        }

        var response = new RegisterResponse(

                usuario.getId(),

                usuario.getNome(),

                usuario.getUsername(),

                usuario.getEmail(),

                usuario.getCpf(),

                usuario.getDataNascimento()

        );

        return response;
    }

    public Usuario toEntityRegister(RegisterRequest request, PasswordEncoder passwordEncoder) {

        if (Util.vazio(request)) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.nome());
        usuario.setUsername(request.username());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setEmail(request.email());
        usuario.setCpf(request.cpf());
        usuario.setDataNascimento(request.dataNascimento());

        return usuario;
    }
}
