package br.com.projeto.spring.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.i18n.MessageResolver;
import jakarta.validation.Validator;

@Component
public class UsuarioValidator extends BaseValidator<Usuario> {

    private final UsuarioRepository usuarioRepository;

    public UsuarioValidator(Validator validator, UsuarioRepository usuarioRepository, MessageResolver messages) {
        super(validator, messages);
        this.usuarioRepository = usuarioRepository;
    }

    private void validarUsuario(Usuario usuario) {
        super.validar(usuario);
    }

    @Override
    public void validarCadastro(Usuario usuario) {
        validarCadastro(List.of(usuario));
    }

    @Override
    public void validarCadastro(List<Usuario> usuarios) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (Usuario usuario : usuarios) {
            validarUsuario(usuario);
            validarPermissaoCriarTornarAdmin(usuario, auth);
            // Adicione outras regras de validação conforme necessário
        }
    }

    @Override
    public void validarAtualizacao(Usuario usuario) throws AccessDeniedException {
        validarAtualizacao(List.of(usuario));
    }

    @Override
    public void validarAtualizacao(List<Usuario> usuariosAtualizados) throws AccessDeniedException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        for (Usuario usuarioAtualizado : usuariosAtualizados) {
            validarUsuario(usuarioAtualizado);
            validarPermissaoTornarAdmin(usuarioAtualizado, auth);
            // Adicione outras regras de validação conforme necessário
        }
    }

    @Override
    public void validarExclusao(Usuario usuario) {
        validarExclusao(List.of(usuario));
    }

    @Override
    public void validarExclusao(List<Usuario> usuarios) {
        // Adicione outras regras de validação conforme necessário
    }

    private void validarPermissaoTornarAdmin(Usuario usuarioAtualizado, Authentication auth)
            throws AccessDeniedException {

        Optional<Usuario> usuarioBanco = usuarioRepository.findById(usuarioAtualizado.getId());

        if (usuarioBanco.isPresent()) {

            boolean eraAdmin = Boolean.TRUE.equals(usuarioBanco.get().isAdmin());
            boolean vaiSerAdmin = Boolean.TRUE.equals(usuarioAtualizado.isAdmin());

            if (!eraAdmin && vaiSerAdmin) {
                validarPermissaoCriarTornarAdmin(usuarioAtualizado, auth);
            }
        }
    }

    private void validarPermissaoCriarTornarAdmin(Usuario usuario, Authentication auth) throws AccessDeniedException {

        if (Util.preenchido(usuario.isAdmin()) && usuario.isAdmin()) {

            boolean temPermissao =
                    auth.getAuthorities().stream().anyMatch(a -> "usuario:create:admin".equals(a.getAuthority()));

            if (!temPermissao) {
                throw new AccessDeniedException(ValidationMessagesKeys.AUTORIZACAO_NAO_PODE_CRIAR_ADMIN);
            }
        }
    }
}
