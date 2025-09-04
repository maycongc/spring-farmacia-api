package br.com.projeto.spring.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioRequest;
import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.laboratorio.LaboratorioResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.service.LaboratorioService;
import br.com.projeto.spring.util.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/laboratorios")
@RequiredArgsConstructor
public class LaboratorioController {

    private final LaboratorioService service;

    /**
     * Busca um laboratório pelo ID.
     *
     * @param id ID do laboratório
     * @return ResponseEntity com os dados do laboratório
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('laboratorio:read')")
    public ResponseEntity<LaboratorioResponse> buscarLaboratorioPorId(

            @PathVariable
            Long id) {

        LaboratorioResponse response = service.buscarLaboratorioPorId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os laboratórios de forma paginada.
     *
     * @param page página solicitada
     * @param pageSize tamanho da página
     * @return ResponseEntity com a lista paginada de laboratórios
     */
    @GetMapping
    @PreAuthorize("hasAuthority('laboratorio:read')")
    public ResponseEntity<PageResponse<LaboratorioResponse>> listarLaboratorios(

            @RequestParam(defaultValue = "0")
            String page,

            @RequestParam(defaultValue = "10")
            String pageSize,

            @RequestParam
            Map<String, String> params) {

        var sortParam = params.get("sortBy");
        var orderParam = params.get("order");

        Sort sort = Sort.unsorted();

        if (Util.preenchido(sortParam)) {
            if (Util.preenchido(orderParam) && orderParam.equals("asc")) {
                sort = Sort.by(sortParam).ascending();
            } else {
                sort = Sort.by(sortParam).descending();
            }
        }

        Pageable paginacao = Util.gerarPaginacao(page, pageSize, sort);
        PageResponse<LaboratorioResponse> response = service.listarLaboratorios(paginacao);

        if (Util.vazio(response.content())) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Lista os remédios de um laboratório específico de forma paginada.
     *
     * @param id ID do laboratório
     * @param page página solicitada
     * @param pageSize tamanho da página
     * @return ResponseEntity com a lista paginada de remédios
     */
    @GetMapping("/{id}/remedios")
    @PreAuthorize("hasAuthority('laboratorio:read') and hasAuthority('remedio:read')")
    public ResponseEntity<PageResponse<RemedioResponse>> listarRemediosPorLaboratorio(

            @PathVariable
            Long id,

            @RequestParam(defaultValue = "0")
            String page,

            @RequestParam(defaultValue = "10")
            String pageSize) {

        Pageable paginacao = Util.gerarPaginacao(page, pageSize, Sort.by("createdAt"));
        PageResponse<RemedioResponse> response = service.listarRemediosPorLaboratorio(id, paginacao);
        if (Util.vazio(response.content())) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Cria um novo laboratório.
     *
     * @param requestDTO dados do laboratório a ser criado
     * @return ResponseEntity com o laboratório criado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('laboratorio:create')")
    public ResponseEntity<LaboratorioResponse> cadastrarLaboratorio(

            @RequestBody
            @Valid
            LaboratorioRequest requestDTO) {

        LaboratorioResponse response = service.cadastrarLaboratorio(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cria vários laboratórios em lote.
     *
     * @param requestDTO lista de laboratórios a serem criados
     * @return ResponseEntity com a lista de laboratórios criados
     */
    @PostMapping("/lote")
    @PreAuthorize("hasAuthority('laboratorio:create')")
    public ResponseEntity<List<LaboratorioResponse>> cadastrarLaboratorioEmLote(

            @RequestBody
            List<LaboratorioRequest> requestDTO) {

        if (requestDTO.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        List<LaboratorioResponse> response = service.cadastrarLaboratorioEmLote(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza os dados de um laboratório existente.
     *
     * @param id ID do laboratório
     * @param requestDTO dados para atualização
     * @return ResponseEntity com o laboratório atualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('laboratorio:update')")
    public ResponseEntity<LaboratorioResponse> atualizarLaboratorio(

            @PathVariable
            Long id,

            @RequestBody
            @Valid
            LaboratorioUpdateRequest requestDTO) {

        LaboratorioResponse response = service.atualizarLaboratorio(id, requestDTO);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Deleta um laboratório pelo ID.
     *
     * @param id ID do laboratório
     * @return ResponseEntity sem conteúdo
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('laboratorio:delete')")
    public ResponseEntity<Void> deletarLaboratorio(

            @PathVariable
            Long id) {

        service.deletarLaboratorio(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Deleta vários laboratórios em lote.
     *
     * @param ids lista de IDs dos laboratórios
     * @return ResponseEntity sem conteúdo
     */
    @DeleteMapping("/lote")
    @PreAuthorize("hasAuthority('laboratorio:delete')")
    public ResponseEntity<Void> deletarLaboratorioEmLote(

            @RequestBody
            List<Long> ids) {

        service.deletarLaboratorioEmLote(ids);
        return ResponseEntity.ok().build();
    }

}
