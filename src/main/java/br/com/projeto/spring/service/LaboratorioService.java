package br.com.projeto.spring.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioRequest;
import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.laboratorio.LaboratorioResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;

/**
 * Serviço responsável pelas operações relacionadas à entidade Laboratório.
 */
public interface LaboratorioService {

    /**
     * Busca um laboratório pelo ID informado.
     *
     * @param id ID do laboratório
     * @return LaboratorioResponse com os dados do laboratório
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     */
    LaboratorioResponse buscarLaboratorioPorId(Long id);

    /**
     * Lista todos os laboratórios de forma paginada.
     *
     * @param paginacao informações de paginação
     * @return PageResponse contendo a lista de laboratórios
     */
    PageResponse<LaboratorioResponse> listarLaboratorios(Pageable paginacao);

    /**
     * Lista os remédios de um laboratório específico de forma paginada.
     *
     * @param id ID do laboratório
     * @param paginacao informações de paginação
     * @return PageResponse contendo a lista de remédios do laboratório
     */
    PageResponse<RemedioResponse> listarRemediosPorLaboratorio(Long id, Pageable paginacao);

    /**
     * Cria um novo laboratório.
     *
     * @param request dados do laboratório a ser criado
     * @return LaboratorioResponse com os dados do laboratório criado
     * @throws IllegalArgumentException se houver erro de validação
     */
    LaboratorioResponse cadastrarLaboratorio(LaboratorioRequest request) throws IllegalArgumentException;

    /**
     * Cria vários laboratórios em lote.
     *
     * @param request lista de laboratórios a serem criados
     * @return lista de LaboratorioResponse com os laboratórios criados
     * @throws IllegalArgumentException se houver erro de validação
     */
    List<LaboratorioResponse> cadastrarLaboratorioEmLote(List<LaboratorioRequest> request)
            throws IllegalArgumentException;

    /**
     * Atualiza os dados de um laboratório existente.
     *
     * @param id ID do laboratório
     * @param request dados para atualização
     * @return LaboratorioResponse com os dados atualizados
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     */
    LaboratorioResponse atualizarLaboratorio(Long id, LaboratorioUpdateRequest request)
            throws ResourceNotFoundException;

    /**
     * Deleta um laboratório pelo ID informado.
     *
     * @param id ID do laboratório
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     * @throws EntityInUseException se o laboratório possuir entidades relacionadas e não puder ser
     *         excluído
     */
    void deletarLaboratorio(Long id) throws ResourceNotFoundException, EntityInUseException;

    /**
     * Deleta vários laboratórios em lote.
     *
     * @param ids lista de IDs dos laboratórios
     * @throws ResourceNotFoundException se algum laboratório não for encontrado
     * @throws EntityInUseException se algum laboratório possuir entidades relacionadas e não puder ser
     *         excluído
     */
    void deletarLaboratorioEmLote(List<Long> ids) throws ResourceNotFoundException, EntityInUseException;
}
