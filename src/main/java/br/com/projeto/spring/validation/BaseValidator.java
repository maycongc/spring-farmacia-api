package br.com.projeto.spring.validation;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.util.Util;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public abstract class BaseValidator<T> {

    private final Validator validator;

    protected void validar(T entidade) {
        Set<ConstraintViolation<T>> violacoes = validator.validate(entidade);

        if (!violacoes.isEmpty()) {
            Map<String, String> errors = violacoes.stream()
                    .collect(Collectors.toMap(v -> v.getPropertyPath().toString(),
                            v -> Util.resolveMensagem(v.getMessage(), v.getPropertyPath().toString()),
                            (msg1, msg2) -> msg1 + ", " + msg2));

            throw new ValidationException("Dados inválidos", errors);
        }
    }

    public abstract void validarCadastro(T entidade);

    public abstract void validarCadastro(List<T> entidades);

    public abstract void validarAtualizacao(T entidade);

    public abstract void validarAtualizacao(List<T> entidades);

    public abstract void validarExclusao(T entidade);

    public abstract void validarExclusao(List<T> entidades);
}
