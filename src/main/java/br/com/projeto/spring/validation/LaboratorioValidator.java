package br.com.projeto.spring.validation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.util.Util;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LaboratorioValidator {

    private final LaboratorioRepository repository;

    /**
     * Valida a criação de uma lista de laboratórios, checando todas as regras de negócio.
     *
     * @param laboratorios Lista de laboratórios a serem validados.
     * @throws IllegalArgumentException se houver emails duplicados.
     */
    public void validaCriacao(List<Laboratorio> laboratorios) {
        validarEmailsDuplicados(laboratorios);
        // Adicione aqui outras validações de criação se necessário
    }

    /**
     * Valida a atualização de uma lista de laboratórios, checando todas as regras de negócio.
     *
     * @param laboratorios Lista de laboratórios a serem atualizados.
     * @throws IllegalArgumentException se houver emails duplicados.
     */
    public void validaAtualizacao(List<Laboratorio> laboratorios) {
        // Adicione aqui outras validações de atualização se necessário
    }

    /**
     * Valida a exclusão de uma lista de laboratórios, checando todas as regras de negócio.
     *
     * @param laboratorios Lista de laboratórios a serem excluídos.
     * @param idsRequisitados Lista de IDs requisitados para exclusão.
     * @throws EntityInUseException se não for permitido excluir algum laboratório por possuir remédios
     *         relacionados.
     * @throws ResourceNotFoundException se algum laboratório não for encontrado.
     */
    public void validaExclusao(List<Laboratorio> laboratorios, List<UUID> idsRequisitados) {

        if (Util.preenchido(idsRequisitados)) {
            validarLaboratoriosEncontrados(idsRequisitados, laboratorios);
        }

        validarRemediosExistentes(laboratorios);
        // Outras validações de exclusão podem ser adicionadas aqui
    }

    /**
     * Valida a criação de um laboratório, checando todas as regras de negócio.
     *
     * @param laboratorio Laboratório a ser validado.
     * @throws IllegalArgumentException se o email já estiver em uso.
     */
    public void validaCriacao(Laboratorio laboratorio) {
        validaCriacao(List.of(laboratorio));
    }

    /**
     * Valida a atualização de um laboratório, checando todas as regras de negócio.
     *
     * @param laboratorio Laboratório a ser atualizado.
     * @throws IllegalArgumentException se o email já estiver em uso por outro laboratório.
     */
    public void validaAtualizacao(Laboratorio laboratorio) {
        validaAtualizacao(List.of(laboratorio));
    }

    /**
     * Valida a exclusão de um laboratório, checando todas as regras de negócio.
     *
     * @param laboratorio Laboratório a ser excluído.
     * @throws EntityInUseException se não for permitido excluir o laboratório por possuir remédios
     *         relacionados.
     */
    public void validaExclusao(Laboratorio laboratorio) {
        validaExclusao(List.of(laboratorio), List.of());
    }

    /**
     * Valida a exclusão de um laboratório, checando todas as regras de negócio.
     *
     * @param laboratorio Laboratório a ser excluído.
     * @param idRequisitado ID do laboratório requisitado para exclusão.
     * @throws EntityInUseException se não for permitido excluir o laboratório por possuir remédios
     *         relacionados.
     */
    public void validaExclusao(Laboratorio laboratorio, UUID idRequisitado) {
        validaExclusao(List.of(laboratorio), List.of(idRequisitado));
    }

    /**
     * Valida se há emails duplicados em uma lista de laboratórios, tanto na própria lista quanto já
     * existentes no banco de dados.
     *
     * @param laboratorios Lista de laboratórios a serem validados.
     * @throws IllegalArgumentException se houver emails duplicados.
     */
    public void validarEmailsDuplicados(List<Laboratorio> laboratorios) throws IllegalArgumentException {

        Set<String> emailsUnicos = new HashSet<>();

        Set<String> emailsDuplicadosNaLista = laboratorios.stream().map(Laboratorio::getEmail)
                .filter(email -> !emailsUnicos.add(email)).collect(Collectors.toSet());

        Set<String> emailsDuplicadosNoBanco = laboratorios.stream().map(Laboratorio::getEmail)
                .filter(email -> repository.existsByEmail(email)).collect(Collectors.toSet());

        Set<String> emailsDuplicados = new HashSet<>(emailsDuplicadosNaLista);
        emailsDuplicados.addAll(emailsDuplicadosNoBanco);

        if (Util.preenchido(emailsDuplicados)) {
            String mensagem = Util.resolveMensagem(ValidationMessagesKeys.LABORATORIO_EMAIL_UNICO,
                    String.join(", ", emailsDuplicados));

            throw new IllegalArgumentException(mensagem);
        }
    }

    /**
     * Valida se existem remédios relacionados a algum laboratório da lista, impedindo a exclusão caso
     * existam.
     *
     * @param laboratorios Lista de laboratórios a serem verificados.
     * @throws EntityInUseException se houver remédios relacionados a algum laboratório.
     */
    private void validarRemediosExistentes(List<Laboratorio> laboratorios) throws IllegalArgumentException {
        StringBuilder mensagemDetalhada = new StringBuilder();

        for (Laboratorio laboratorio : laboratorios) {
            boolean existeRemediosRelacionados = repository.existsByIdAndRemediosIsNotEmpty(laboratorio.getId());

            if (existeRemediosRelacionados) {
                String nomeLab = laboratorio.getNome() != null ? laboratorio.getNome() : laboratorio.getId().toString();
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
            String mensagem = Util.resolveMensagem(ValidationMessagesKeys.LABORATORIO_EXCLUSAO_REMEDIOS_EXISTENTES,
                    mensagemDetalhada.toString().trim());
            throw new EntityInUseException(mensagem);
        }
    }

    /**
     * Valida se todos os IDs requisitados foram encontrados na lista de laboratórios.
     *
     * @param idsRequisitados Lista de IDs requisitados.
     * @param encontrados Lista de laboratórios encontrados.
     * @throws ResourceNotFoundException se algum laboratório não for encontrado.
     */
    public void validarLaboratoriosEncontrados(List<UUID> idsRequisitados, List<Laboratorio> encontrados) {
        List<String> encontradosIds = encontrados.stream().map(lab -> lab.getId().toString()).toList();

        List<String> idsNaoEncontrados =
                idsRequisitados.stream().map(UUID::toString).filter(id -> !encontradosIds.contains(id)).toList();

        if (!idsNaoEncontrados.isEmpty()) {
            String mensagem = Util.resolveMensagem(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO,
                    String.join(", ", idsNaoEncontrados));
            throw new ResourceNotFoundException(mensagem);
        }
    }

}
