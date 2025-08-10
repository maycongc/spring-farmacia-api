package br.com.projeto.spring.mapper;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.util.Util;

@Component
public class UsuarioMapper {

    /**
     * Converte UsuarioRequest para Usuario (entidade).
     * 
     * @param request DTO de entrada
     * @param passwordEncoder
     * @return entidade Usuario
     */
    public Usuario toEntity(UsuarioRequest request, PasswordEncoder passwordEncoder) {
        if (request == null)
            return null;

        Usuario usuario = new Usuario();

        usuario.setUsername(request.username());
        usuario.setSenha(passwordEncoder.encode(request.senha()));
        usuario.setNome(request.nome());
        usuario.setEmail(request.email());
        usuario.setCpf(request.cpf());
        usuario.setDataNascimento(request.dataNascimento());
        usuario.setTelefone(Util.padronizarTelefone(request.telefone()));
        usuario.setEndereco(request.endereco());
        usuario.setComplemento(request.complemento());
        usuario.setCidade(request.cidade());
        usuario.setUf(request.uf());
        usuario.setCep(request.cep());
        usuario.setAdmin(request.isAdmin());

        return usuario;
    }

    /**
     * Converte Usuario (entidade) para UsuarioResponse.
     * 
     * @param usuario entidade Usuario
     * @return DTO de resposta
     */
    public UsuarioResponse toResponse(Usuario usuario) {
        if (Util.vazio(usuario)) {
            return null;
        }

        return new UsuarioResponse(

                usuario.getId(),

                usuario.getUsername(),

                usuario.getNome(),

                usuario.getEmail(),

                usuario.getCpf(),

                usuario.getDataNascimento(),

                usuario.getTelefone(),

                usuario.getCep(),

                usuario.getEndereco(),

                usuario.getComplemento(),

                usuario.getCidade(),

                usuario.getUf(),

                usuario.getCreatedAt(),

                usuario.getUpdatedAt()

        );
    }

    public void updateEntity(Usuario usuario, UsuarioUpdateRequest request) {

        if (Util.preenchido(request.username())) {
            usuario.setUsername(request.username());
        }

        if (Util.preenchido(request.nome())) {
            usuario.setNome(request.nome());
        }

        if (Util.preenchido(request.email())) {
            usuario.setEmail(request.email());
        }

        if (Util.preenchido(request.dataNascimento())) {
            usuario.setDataNascimento(request.dataNascimento());
        }

        if (Util.preenchido(request.telefone())) {
            usuario.setTelefone(request.telefone());
        }

        if (Util.preenchido(request.cep())) {
            usuario.setCep(request.cep());
        }

        if (Util.preenchido(request.endereco())) {
            usuario.setEndereco(request.endereco());
        }

        if (Util.preenchido(request.complemento())) {
            usuario.setComplemento(request.complemento());
        }

        if (Util.preenchido(request.cidade())) {
            usuario.setCidade(request.cidade());
        }

        if (Util.preenchido(request.uf())) {
            usuario.setUf(request.uf());
        }

        if (Util.preenchido(request.isAdmin())) {
            usuario.setAdmin(request.isAdmin());
        }
    }

    public Usuario copy(Usuario usuarioBanco) {

        if (Util.vazio(usuarioBanco)) {
            return null;
        }

        Usuario usuario = new Usuario();

        usuario.setId(usuarioBanco.getId());
        usuario.setUsername(usuarioBanco.getUsername());
        usuario.setSenha(usuarioBanco.getSenha());
        usuario.setNome(usuarioBanco.getNome());
        usuario.setEmail(usuarioBanco.getEmail());
        usuario.setCpf(usuarioBanco.getCpf());
        usuario.setDataNascimento(usuarioBanco.getDataNascimento());
        usuario.setTelefone(usuarioBanco.getTelefone());
        usuario.setEndereco(usuarioBanco.getEndereco());
        usuario.setComplemento(usuarioBanco.getComplemento());
        usuario.setCidade(usuarioBanco.getCidade());
        usuario.setUf(usuarioBanco.getUf());
        usuario.setCep(usuarioBanco.getCep());
        usuario.setAdmin(usuarioBanco.isAdmin());
        usuario.setCreatedAt(usuarioBanco.getCreatedAt());
        usuario.setUpdatedAt(usuarioBanco.getUpdatedAt());
        usuario.setGruposUsuario(usuarioBanco.getGruposUsuario());
        usuario.setPermissoes(usuarioBanco.getPermissoes());

        return usuario;
    }

}
