package br.com.projeto.spring.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.i18n.MessageResolver;
import jakarta.validation.Validator;

@Component
public class LaboratorioValidator extends BaseValidator<Laboratorio> {

    private final LaboratorioRepository repository;
    private final MessageResolver messages;

    public LaboratorioValidator(Validator validator, LaboratorioRepository repository, MessageResolver messages) {
        super(validator, messages);
        this.repository = repository;
        this.messages = messages;
    }

    private void validarLaboratorio(Laboratorio laboratorio) {
        super.validar(laboratorio);
    }

    @Override
    public void validarCadastro(Laboratorio laboratorio) {
        validarCadastro(List.of(laboratorio));
    }

    @Override
    public void validarCadastro(List<Laboratorio> laboratorios) {

        for (Laboratorio laboratorio : laboratorios) {
            validarLaboratorio(laboratorio);
        }

        validarEmailsDuplicados(laboratorios);
        // Adicione aqui outras validações de cadastro se necessário
    }

    @Override
    public void validarAtualizacao(Laboratorio laboratorio) {
        validarAtualizacao(List.of(laboratorio));
    }

    @Override
    public void validarAtualizacao(List<Laboratorio> laboratorios) {

        for (Laboratorio laboratorio : laboratorios) {
            validarLaboratorio(laboratorio);
            // Adicione aqui outras validações de atualização se necessário
        }
    }

    @Override
    public void validarExclusao(Laboratorio laboratorio) {
        validarExclusao(List.of(laboratorio), List.of());
    }

    @Override
    public void validarExclusao(List<Laboratorio> laboratorios) {
        validarExclusao(laboratorios, List.of());
    }

    public void validarExclusao(Laboratorio laboratorio, Long idRequisitado) {
        validarExclusao(List.of(laboratorio), List.of(idRequisitado));
    }

    public void validarExclusao(List<Laboratorio> laboratorios, List<Long> idsRequisitados) {

        if (Util.preenchido(idsRequisitados)) {
            validarLaboratoriosEncontrados(idsRequisitados, laboratorios);
        }

        validarRemediosExistentes(laboratorios);
        // Adicione aqui outras validações de exclusão se necessário
    }

    private void validarEmailsDuplicados(List<Laboratorio> laboratorios) throws IllegalArgumentException {

        Set<String> emailsUnicos = new HashSet<>();

        Set<String> emailsDuplicadosNaLista = laboratorios.stream().map(Laboratorio::getEmail)
                .filter(email -> !emailsUnicos.add(email)).collect(Collectors.toSet());

        Set<String> emailsDuplicadosNoBanco = laboratorios.stream().map(Laboratorio::getEmail)
                .filter(email -> repository.existsByEmail(email)).collect(Collectors.toSet());

        Set<String> emailsDuplicados = new HashSet<>(emailsDuplicadosNaLista);
        emailsDuplicados.addAll(emailsDuplicadosNoBanco);

        if (Util.preenchido(emailsDuplicados)) {

            String mensagem = messages.get(ValidationMessagesKeys.LABORATORIO_EMAIL_UNICO);

            throw new ValidationException(mensagem);
        }
    }

    private void validarRemediosExistentes(List<Laboratorio> laboratorios) throws EntityInUseException {
        StringBuilder mensagemDetalhada = new StringBuilder();

        for (Laboratorio laboratorio : laboratorios) {
            boolean existeRemediosRelacionados = repository.existsByIdAndRemediosIsNotEmpty(laboratorio.getId());

            if (existeRemediosRelacionados) {

                String nomeLab = Util.preenchido(() -> laboratorio.getNome()) ? laboratorio.getNome()
                        : laboratorio.getId().toString();

                mensagemDetalhada.append("- ").append(nomeLab).append(":\n");

                if (laboratorio.getRemedios() != null && !laboratorio.getRemedios().isEmpty()) {
                    laboratorio.getRemedios().forEach(remedio -> {
                        mensagemDetalhada.append("    • [").append(remedio.getId()).append("] ")
                                .append(remedio.getNome()).append("\n");
                    });
                }
            }
        }

        if (mensagemDetalhada.length() > 0) {
            String mensagem = messages.get(ValidationMessagesKeys.LABORATORIO_EXCLUSAO_REMEDIOS_EXISTENTES,
                    mensagemDetalhada.toString().trim());
            throw new EntityInUseException(mensagem);
        }
    }

    private void validarLaboratoriosEncontrados(List<Long> idsRequisitados, List<Laboratorio> encontrados)
            throws ResourceNotFoundException {
        List<Long> encontradosIds = encontrados.stream().map(Laboratorio::getId).toList();

        List<Long> idsNaoEncontrados = idsRequisitados.stream().filter(id -> !encontradosIds.contains(id)).toList();

        if (!idsNaoEncontrados.isEmpty()) {
            String mensagem = messages.get(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO,
                    String.join(", ", idsNaoEncontrados.stream().map(String::valueOf).toList()));
            throw new ResourceNotFoundException(mensagem);
        }
    }

}
