package br.com.projeto.spring.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioRequest;
import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.laboratorio.LaboratorioResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.service.LaboratorioService;
import br.com.projeto.spring.util.Util;
import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class LaboratorioServiceCacheImpl implements LaboratorioService {

    public final LaboratorioServiceImpl laboratorioServiceImpl;

    private final Map<Long, LaboratorioResponse> cacheLaboratorio = new ConcurrentHashMap<>();
    private final Map<String, PageResponse<LaboratorioResponse>> cachePageLaboratorios = new ConcurrentHashMap<>();
    private final Map<String, PageResponse<RemedioResponse>> cachePageRemedios = new ConcurrentHashMap<>();

    /**
     * Busca um laboratório pelo ID, utilizando cache.
     * 
     * @param id identificador do laboratório
     * @return LaboratorioResponse correspondente
     */
    @Override
    public LaboratorioResponse buscarLaboratorioPorId(Long id) {
        return cacheLaboratorio.computeIfAbsent(id, laboratorioServiceImpl::buscarLaboratorioPorId);
    }

    /**
     * Lista todos os laboratórios paginados, utilizando cache para as páginas.
     * 
     * @param paginacao informações de paginação e ordenação
     * @return página de LaboratorioResponse
     */
    @Override
    public PageResponse<LaboratorioResponse> listarLaboratorios(Pageable paginacao) {
        String cacheKey = "page:" + paginacao.getPageNumber() + ":" + paginacao.getPageSize() + ":"
                + paginacao.getSort().toString();
        return cachePageLaboratorios.computeIfAbsent(cacheKey,
                k -> laboratorioServiceImpl.listarLaboratorios(paginacao));
    }

    /**
     * Lista os remédios de um laboratório específico, paginado e com cache por laboratório.
     * 
     * @param id identificador do laboratório
     * @param paginacao informações de paginação e ordenação
     * @return página de RemedioResponse
     */
    @Override
    public PageResponse<RemedioResponse> listarRemediosPorLaboratorio(Long id, Pageable paginacao) {
        String cacheKey = String.format("remedios:%s:%d:%d:%s", id, paginacao.getPageNumber(), paginacao.getPageSize(),
                paginacao.getSort().toString());

        return cachePageRemedios.computeIfAbsent(cacheKey,
                k -> laboratorioServiceImpl.listarRemediosPorLaboratorio(id, paginacao));
    }

    /**
     * Cria um novo laboratório e atualiza o cache.
     * 
     * @param request dados do laboratório a ser criado
     * @return LaboratorioResponse criado
     * @throws IllegalArgumentException caso dados inválidos
     */
    @Override
    public LaboratorioResponse cadastrarLaboratorio(LaboratorioRequest request) throws IllegalArgumentException {
        LaboratorioResponse response = laboratorioServiceImpl.cadastrarLaboratorio(request);

        if (Util.preenchido(() -> response.id())) {
            cacheLaboratorio.put(response.id(), response);
        }

        cachePageLaboratorios.clear(); // Limpa cache de páginas pois pode ter mudado
        return response;
    }

    /**
     * Cria vários laboratórios em lote e atualiza o cache.
     * 
     * @param request lista de laboratórios a serem criados
     * @return lista de LaboratorioResponse criados
     * @throws IllegalArgumentException caso dados inválidos
     */
    @Override
    public List<LaboratorioResponse> cadastrarLaboratorioEmLote(List<LaboratorioRequest> request)
            throws IllegalArgumentException {

        List<LaboratorioResponse> responses = laboratorioServiceImpl.cadastrarLaboratorioEmLote(request);

        if (Util.preenchido(responses)) {
            for (LaboratorioResponse response : responses) {
                if (Util.preenchido(() -> response.id())) {
                    cacheLaboratorio.put(response.id(), response);
                }
            }
        }

        cachePageLaboratorios.clear();
        return responses;
    }

    /**
     * Atualiza um laboratório existente e atualiza o cache.
     * 
     * @param id identificador do laboratório
     * @param request dados para atualização
     * @return LaboratorioResponse atualizado
     * @throws ResourceNotFoundException caso laboratório não exista
     */
    @Override
    public LaboratorioResponse atualizarLaboratorio(Long id, LaboratorioUpdateRequest request)
            throws ResourceNotFoundException {

        LaboratorioResponse response = laboratorioServiceImpl.atualizarLaboratorio(id, request);

        if (Util.preenchido(() -> response.id())) {
            cacheLaboratorio.put(response.id(), response);
        }

        cachePageLaboratorios.clear();
        cachePageRemedios.keySet().removeIf(key -> key.startsWith("remedios:" + id + ":"));
        return response;
    }

    /**
     * Remove um laboratório pelo ID, removendo do cache e limpando páginas relacionadas.
     * 
     * @param id identificador do laboratório
     * @throws ResourceNotFoundException caso laboratório não exista
     * @throws EntityInUseException caso o laboratório possua entidades relacionadas e não possa ser
     *         excluído
     */
    @Override
    public void deletarLaboratorio(Long id) throws ResourceNotFoundException, EntityInUseException {

        laboratorioServiceImpl.deletarLaboratorio(id);

        cacheLaboratorio.remove(id);
        cachePageLaboratorios.clear();
        cachePageRemedios.keySet().removeIf(key -> key.startsWith("remedios:" + id + ":"));
    }

    /**
     * Remove vários laboratórios em lote, removendo do cache e limpando páginas relacionadas.
     * 
     * @param ids lista de identificadores dos laboratórios
     * @throws ResourceNotFoundException caso algum laboratório não exista
     * @throws EntityInUseException caso algum laboratório possua entidades relacionadas e não possa ser
     *         excluído
     */
    @Override
    public void deletarLaboratorioEmLote(List<Long> ids) throws ResourceNotFoundException, EntityInUseException {

        laboratorioServiceImpl.deletarLaboratorioEmLote(ids);

        if (Util.preenchido(ids)) {
            for (Long id : ids) {
                cacheLaboratorio.remove(id);
                cachePageRemedios.keySet().removeIf(key -> key.startsWith("remedios:" + id + ":"));
            }
        }

        cachePageLaboratorios.clear();
    }
}
