package br.com.projeto.spring.validation;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Remedio;
import jakarta.validation.Validator;
import br.com.projeto.spring.i18n.MessageResolver;

@Component
public class RemedioValidator extends BaseValidator<Remedio> {

    public RemedioValidator(Validator validator, MessageResolver messages) {
        super(validator, messages);
    }

    @Override
    public void validarCadastro(Remedio remedio) {
        validarCadastro(List.of(remedio));
    }

    @Override
    public void validarCadastro(List<Remedio> remedios) {
        remedios.forEach(super::validar);
    }

    @Override
    public void validarAtualizacao(Remedio remedio) {
        validarAtualizacao(List.of(remedio));
    }

    @Override
    public void validarAtualizacao(List<Remedio> remedios) {
        remedios.forEach(super::validar);
    }

    @Override
    public void validarExclusao(Remedio remedio) {
        validarExclusao(List.of(remedio));
    }

    @Override
    public void validarExclusao(List<Remedio> remedios) {
        // Adicione aqui novas validações conforme necessário
    }

}
