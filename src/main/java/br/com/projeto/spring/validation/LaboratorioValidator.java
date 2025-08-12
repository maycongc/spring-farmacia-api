package br.com.projeto.spring.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.util.Util;
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

        validarRemediosExistentesJson(laboratorios);

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

    public void validarRemediosExistentesJson(List<Laboratorio> laboratorios) {
        Map<String, Set<String>> laboratoriosRemedios = new HashMap<>();

        for (Laboratorio laboratorio : laboratorios) {

            Laboratorio laboratorioBanco = repository.findByIdAndRemediosIsNotEmpty(laboratorio.getId()).orElse(null);

            if (Util.preenchido(laboratorioBanco)) {

                String nomeLab = laboratorio.getNome();

                Set<String> remediosSet = new HashSet<>();

                if (Util.preenchido(laboratorioBanco.getRemedios())) {
                    laboratorioBanco.getRemedios().forEach(remedio -> {
                        remediosSet.add("[" + remedio.getId() + "] " + remedio.getNome());
                    });
                }

                laboratoriosRemedios.put(nomeLab, remediosSet);
            }
        }

        if (!laboratoriosRemedios.isEmpty()) {
            throw new EntityInUseException(laboratoriosRemedios);
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
