package br.com.projeto.spring.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.dto.request.remedio.RemedioRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.domain.model.Remedio;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.mapper.RemedioMapper;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.repository.RemedioRepository;
import br.com.projeto.spring.service.RemedioService;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.validation.RemedioValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RemedioServiceImpl implements RemedioService {

    private final RemedioRepository repository;
    private final LaboratorioRepository laboratorioRepository;
    private final RemedioMapper mapper;
    private final RemedioValidator validator;

    @Override
    /**
     * Busca um remédio pelo seu ID.
     *
     * @param id ID do remédio.
     * @return DTO de resposta do remédio.
     * @throws ResourceNotFoundException se o remédio não for encontrado.
     */
    @Transactional(readOnly = true)
    public RemedioResponse buscarRemedioPorId(Long id) throws ResourceNotFoundException {

        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.REMEDIO_NAO_ENCONTRADO));

        return mapper.toResponse(remedio);
    }

    @Override
    /**
     * Lista todos os remédios com paginação.
     *
     * @param paginacao informações de paginação.
     * @return Página de DTOs de resposta de remédio.
     */
    @Transactional(readOnly = true)
    public PageResponse<RemedioResponse> listarRemedios(Pageable paginacao) {

        Page<Remedio> page = repository.findAll(paginacao);
        return Util.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    /**
     * Cria um novo remédio.
     *
     * @param requestDTO DTO de requisição para criação do remédio.
     * @return DTO de resposta do remédio criado.
     * @throws ResourceNotFoundException se o laboratório informado não existir.
     */
    public RemedioResponse cadastrarRemedio(RemedioRequest requestDTO) throws ResourceNotFoundException {

        Long id = requestDTO.laboratorio().id();

        Laboratorio laboratorio = laboratorioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

        Remedio remedio = mapper.toEntity(requestDTO);
        remedio.setLaboratorio(laboratorio);
        validator.validarCadastro(remedio);

        repository.save(remedio);

        return mapper.toResponse(remedio);
    }

    @Override
    @Transactional
    /**
     * Cria vários remédios em lote.
     *
     * @param requestDTO Lista de DTOs de requisição para criação dos remédios.
     * @return Lista de DTOs de resposta dos remédios criados.
     * @throws ResourceNotFoundException se algum laboratório informado não existir.
     */
    public List<RemedioResponse> cadastrarRemediosEmLote(List<RemedioRequest> requestDTO)
            throws ResourceNotFoundException {

        List<Remedio> remedios = requestDTO.stream().map(dto -> mapper.toEntity(dto)).toList();

        remedios.forEach(remedio -> {
            Laboratorio laboratorio = laboratorioRepository.findById(remedio.getLaboratorio().getId()).orElseThrow(
                    () -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));

            remedio.setLaboratorio(laboratorio);
        });

        validator.validarCadastro(remedios);
        repository.saveAll(remedios);

        return remedios.stream().map(mapper::toResponse).toList();
    }

    @Override
    @Transactional
    /**
     * Atualiza um remédio existente.
     *
     * @param id ID do remédio a ser atualizado.
     * @param remedioRequest DTO de requisição com os dados para atualização.
     * @return DTO de resposta do remédio atualizado.
     * @throws ResourceNotFoundException se o remédio ou laboratório não for encontrado.
     */
    public RemedioResponse atualizarRemedio(Long id, RemedioRequest request) throws ResourceNotFoundException {

        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.REMEDIO_NAO_ENCONTRADO));

        Laboratorio laboratorio = null;

        if (Util.preenchido(() -> request.laboratorio().id())) {

            Long laboratorioId = request.laboratorio().id();

            laboratorio = laboratorioRepository.findById(laboratorioId).orElseThrow(
                    () -> new ResourceNotFoundException(ValidationMessagesKeys.LABORATORIO_NAO_ENCONTRADO));
        }

        mapper.updateEntity(remedio, request, laboratorio);
        validator.validarAtualizacao(remedio);
        repository.save(remedio);

        return mapper.toResponse(remedio);
    }

    @Override
    @Transactional
    /**
     * Remove um remédio pelo seu ID.
     *
     * @param id ID do remédio a ser removido.
     * @throws ResourceNotFoundException se o remédio não for encontrado.
     */
    public void deletarRemedio(Long id) throws ResourceNotFoundException {

        Remedio remedio = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.REMEDIO_NAO_ENCONTRADO));

        validator.validarExclusao(remedio);
        repository.delete(remedio);
    }
}
