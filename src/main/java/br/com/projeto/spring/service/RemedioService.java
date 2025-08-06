package br.com.projeto.spring.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import br.com.projeto.spring.domain.dto.request.remedio.RemedioRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.exception.ResourceNotFoundException;

public interface RemedioService {

    /**
     * Cria um novo remédio.
     *
     * @param remedioRequest DTO de requisição para criação do remédio.
     * @return DTO de resposta do remédio criado.
     * @throws ResourceNotFoundException se o laboratório informado não existir.
     */
    RemedioResponse cadastrarRemedio(RemedioRequest remedioRequest) throws ResourceNotFoundException;

    /**
     * Cria vários remédios em lote.
     *
     * @param requestDTO Lista de DTOs de requisição para criação dos remédios.
     * @return Lista de DTOs de resposta dos remédios criados.
     * @throws ResourceNotFoundException se algum laboratório informado não existir.
     */
    List<RemedioResponse> cadastrarRemediosEmLote(List<RemedioRequest> requestDTO) throws ResourceNotFoundException;

    /**
     * Busca um remédio pelo seu ID.
     *
     * @param id ID do remédio.
     * @return DTO de resposta do remédio.
     * @throws ResourceNotFoundException se o remédio não for encontrado.
     */
    RemedioResponse buscarRemedioPorId(String id) throws ResourceNotFoundException;

    /**
     * Lista todos os remédios com paginação.
     *
     * @param paginacao informações de paginação.
     * @return Página de DTOs de resposta de remédio.
     */
    PageResponse<RemedioResponse> listarRemedios(Pageable paginacao);

    /**
     * Atualiza um remédio existente.
     *
     * @param id ID do remédio a ser atualizado.
     * @param remedioRequest DTO de requisição com os dados para atualização.
     * @return DTO de resposta do remédio atualizado.
     * @throws ResourceNotFoundException se o remédio ou laboratório não for encontrado.
     */
    RemedioResponse atualizarRemedio(String id, RemedioRequest remedioRequest) throws ResourceNotFoundException;

    /**
     * Remove um remédio pelo seu ID.
     *
     * @param id ID do remédio a ser removido.
     * @throws ResourceNotFoundException se o remédio não for encontrado.
     */
    void deletarRemedio(String id) throws ResourceNotFoundException;
}
