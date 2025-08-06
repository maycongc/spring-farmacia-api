package br.com.projeto.spring.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
import jakarta.transaction.Transactional;
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
    public LaboratorioResponse buscarLaboratorioPorId(String id) {

        Laboratorio laboratorio = repository.findById(UUID.fromString(id))
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
    public PageResponse<RemedioResponse> listarRemediosPorLaboratorio(String laboratorioId, Pageable paginacao) {

        Page<Remedio> page = remedioRepository.findByLaboratorioId(UUID.fromString(laboratorioId), paginacao);
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
    public LaboratorioResponse cadastrarLaboratorio(LaboratorioRequest request) throws IllegalArgumentException {

        Laboratorio laboratorio = mapper.toEntity(request);

        validator.validaCriacao(laboratorio);

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
    public List<LaboratorioResponse> cadastrarLaboratorioEmLote(List<LaboratorioRequest> request)
            throws IllegalArgumentException {

        List<Laboratorio> laboratorios = request.stream().map(mapper::toEntity).toList();

        validator.validaCriacao(laboratorios);

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
    public LaboratorioResponse atualizarLaboratorio(String id, LaboratorioUpdateRequest request)
            throws ResourceNotFoundException {
        UUID uuid = UUID.fromString(id);

        Laboratorio laboratorio = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        mapper.updateEntity(laboratorio, request);
        validator.validaAtualizacao(laboratorio);

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
    public void deletarLaboratorio(String id) throws ResourceNotFoundException, IllegalArgumentException {

        UUID uuid = UUID.fromString(id);

        Laboratorio laboratorio = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        validator.validaExclusao(laboratorio, uuid);
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
    public void deletarLaboratorioEmLote(List<String> ids) throws ResourceNotFoundException, IllegalArgumentException {
        List<UUID> uuids = ids.stream().map(UUID::fromString).toList();
        List<Laboratorio> laboratorios = repository.findAllById(uuids);
        validator.validaExclusao(laboratorios, uuids);
        repository.deleteAll(laboratorios);
    }
}
