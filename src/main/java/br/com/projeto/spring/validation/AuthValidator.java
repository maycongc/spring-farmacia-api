package br.com.projeto.spring.validation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.util.Util;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

@Component
public class AuthValidator {

    private final Validator validator;
    private final MessageResolver messages;
    private final UsuarioRepository usuarioRepository;

    private Map<String, String> erros;

    public AuthValidator(Validator validator, UsuarioRepository usuarioRepository, MessageResolver messages) {
        this.validator = validator;
        this.messages = messages;
        this.usuarioRepository = usuarioRepository;
    }

    private void validar(Usuario usuario) {
        Set<ConstraintViolation<Usuario>> violacoes = validator.validate(usuario);

        if (!violacoes.isEmpty()) {
            erros = violacoes.stream()
                    .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                            v -> messages.get(v.getMessage(), v.getPropertyPath().toString()),
                            (msg1, msg2) -> msg1 + ", " + msg2));

            throw new ValidationException("Dados inválidos", erros);
        }
    }

    private void validarUsuario(Usuario usuario) {
        validar(usuario);
    }

    public void validarCadastro(Usuario usuario) {
        erros = new HashMap<>();

        validarUsuario(usuario);
        validarUsernameExisteste(usuario);
        validarEmailExisteste(usuario);
        validarCpfExisteste(usuario);
        // Adicione outras regras de validação conforme necessário

        if (Util.preenchido(erros)) {
            throw new ValidationException("Erro de validação", erros);
        }
    }

    private void validaValorCampoEmUso(boolean existe, String campo) {
        if (existe) {
            erros.put(campo, "Valor do campo já está em uso.");
        }
    }

    private void validarUsernameExisteste(Usuario usuario) {
        var existe = usuarioRepository.existsByUsername(usuario.getUsername());
        validaValorCampoEmUso(existe, "username");
    }

    private void validarEmailExisteste(Usuario usuario) {
        var existe = usuarioRepository.existsByEmail(usuario.getEmail());
        validaValorCampoEmUso(existe, "email");
    }

    private void validarCpfExisteste(Usuario usuario) {
        var existe = usuarioRepository.existsByCpf(usuario.getCpf());
        validaValorCampoEmUso(existe, "cpf");
    }

}
