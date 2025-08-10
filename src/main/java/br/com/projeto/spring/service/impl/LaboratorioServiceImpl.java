package br.com.projeto.spring.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioRequest;
import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.laboratorio.LaboratorioResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.domain.model.Remedio;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.mapper.LaboratorioMapper;
import br.com.projeto.spring.mapper.RemedioMapper;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.repository.RemedioRepository;
import br.com.projeto.spring.service.LaboratorioService;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.validation.LaboratorioValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LaboratorioServiceImpl implements LaboratorioService {

    private final LaboratorioRepository repository;
    private final RemedioRepository remedioRepository;

    private final LaboratorioMapper mapper;
    private final RemedioMapper remedioMapper;

    private final LaboratorioValidator validator;

    @Override
    /**
     * Busca um laboratório pelo ID informado.
     *
     * @param id ID do laboratório
     * @return LaboratorioResponse com os dados do laboratório
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "laboratorio", key = "#id")
    public LaboratorioResponse buscarLaboratorioPorId(Long id) {

        Laboratorio laboratorio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        return mapper.toResponse(laboratorio);
    }

    @Override
    /**
     * Lista todos os laboratórios de forma paginada.
     *
     * @param pageable informações de paginação
     * @return PageResponse contendo a lista de laboratórios
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "laboratorioPages",
            key = "#pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()")
    public PageResponse<LaboratorioResponse> listarLaboratorios(Pageable pageable) {

        Page<Laboratorio> page = repository.findAll(pageable);
        return Util.toPageResponse(page, mapper::toResponse);
    }

    @Override
    /**
     * Lista os remédios de um laboratório específico de forma paginada.
     *
     * @param laboratorioId ID do laboratório
     * @param paginacao informações de paginação
     * @return PageResponse contendo a lista de remédios do laboratório
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "laboratorioRemedios",
            key = "#laboratorioId + ':' + #paginacao.pageNumber + ':' + #paginacao.pageSize + ':' + #paginacao.sort.toString()")
    public PageResponse<RemedioResponse> listarRemediosPorLaboratorio(Long laboratorioId, Pageable paginacao) {

        Page<Remedio> page = remedioRepository.findByLaboratorioId(laboratorioId, paginacao);
        return Util.toPageResponse(page, remedioMapper::toResponse);
    }

    @Override
    @Transactional
    /**
     * Cria um novo laboratório.
     *
     * @param request dados do laboratório a ser criado
     * @return LaboratorioResponse com os dados do laboratório criado
     * @throws IllegalArgumentException se houver erro de validação
     */
    @Caching(evict = { @CacheEvict(cacheNames = { "laboratorioPages", "laboratorioRemedios" }, allEntries = true) },
            put = { @CachePut(cacheNames = "laboratorio", key = "#result.id()",
                    condition = "#result != null && #result.id() != null") })
    public LaboratorioResponse cadastrarLaboratorio(LaboratorioRequest request) throws IllegalArgumentException {

        Laboratorio laboratorio = mapper.toEntity(request);
        validator.validarCadastro(laboratorio);
        repository.save(laboratorio);

        return mapper.toResponse(laboratorio);
    }

    @Override
    @Transactional
    /**
     * Cria vários laboratórios em lote.
     *
     * @param request lista de laboratórios a serem criados
     * @return lista de LaboratorioResponse com os laboratórios criados
     * @throws IllegalArgumentException se houver erro de validação
     */
    @CacheEvict(cacheNames = { "laboratorioPages", "laboratorioRemedios" }, allEntries = true)
    public List<LaboratorioResponse> cadastrarLaboratorioEmLote(List<LaboratorioRequest> request)
            throws IllegalArgumentException {

        List<Laboratorio> laboratorios = request.stream().map(mapper::toEntity).toList();
        validator.validarCadastro(laboratorios);
        repository.saveAll(laboratorios);

        return laboratorios.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    /**
     * Atualiza os dados de um laboratório existente.
     *
     * @param id ID do laboratório
     * @param request dados para atualização
     * @return LaboratorioResponse com os dados atualizados
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     */
    @CacheEvict(cacheNames = { "laboratorioPages", "laboratorioRemedios" }, allEntries = true)
    @CachePut(cacheNames = "laboratorio", key = "#id")
    public LaboratorioResponse atualizarLaboratorio(Long id, LaboratorioUpdateRequest request)
            throws ResourceNotFoundException {

        Laboratorio laboratorio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        mapper.updateEntity(laboratorio, request);
        validator.validarAtualizacao(laboratorio);
        repository.save(laboratorio);

        return mapper.toResponse(laboratorio);
    }

    @Override
    @Transactional
    /**
     * Deleta um laboratório pelo ID informado.
     *
     * @param id ID do laboratório
     * @throws ResourceNotFoundException se o laboratório não for encontrado
     * @throws EntityInUseException se não for possível excluir por possuir entidades relacionadas
     */
    @Caching(evict = { @CacheEvict(cacheNames = "laboratorio", key = "#id"),
            @CacheEvict(cacheNames = { "laboratorioPages", "laboratorioRemedios" }, allEntries = true) })
    public void deletarLaboratorio(Long id) throws ResourceNotFoundException, IllegalArgumentException {

        Laboratorio laboratorio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        validator.validarExclusao(laboratorio, id);
        repository.delete(laboratorio);
    }

    @Override
    @Transactional
    /**
     * Deleta vários laboratórios em lote.
     *
     * @param ids lista de IDs dos laboratórios
     * @throws ResourceNotFoundException se algum laboratório não for encontrado
     * @throws EntityInUseException se não for possível excluir por possuir entidades relacionadas
     */
    @CacheEvict(cacheNames = { "laboratorio", "laboratorioPages", "laboratorioRemedios" }, allEntries = true)
    public void deletarLaboratorioEmLote(List<Long> ids) throws ResourceNotFoundException, IllegalArgumentException {
        List<Laboratorio> laboratorios = repository.findAllById(ids);
        validator.validarExclusao(laboratorios, ids);
        repository.deleteAll(laboratorios);
    }
}
