package br.com.projeto.spring.validation;

import java.util.List;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Remedio;
import jakarta.validation.Validator;

@Component
public class RemedioValidator extends BaseValidator<Remedio> {

    private final Validator validator;

    public RemedioValidator(Validator validator) {
        super(validator);
        this.validator = validator;
    }

    private void validarRemedio(Remedio remedio) {
        super.validar(remedio);
    }

    @Override
    public void validarCadastro(Remedio remedio) {
        validarCadastro(List.of(remedio));
    }

    @Override
    public void validarCadastro(List<Remedio> remedios) {
        for (Remedio remedio : remedios) {
            validarRemedio(remedio);

            // Adicione aqui novas validações conforme necessário
        }
    }

    @Override
    public void validarAtualizacao(Remedio remedio) {
        validarAtualizacao(List.of(remedio));
    }

    @Override
    public void validarAtualizacao(List<Remedio> remedios) {
        for (Remedio remedio : remedios) {
            validarRemedio(remedio);

            // Adicione aqui novas validações conforme necessário
        }
    }

    @Override
    public void validarExclusao(Remedio remedio) {
        validarExclusao(List.of(remedio));
    }

    @Override
    public void validarExclusao(List<Remedio> remedios) {
        for (Remedio remedio : remedios) {
            // Adicione aqui novas validações conforme necessário
        }
    }

}
