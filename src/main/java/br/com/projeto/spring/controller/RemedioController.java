package br.com.projeto.spring.controller;

import java.util.List;

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

import br.com.projeto.spring.domain.dto.request.remedio.RemedioRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.service.RemedioService;
import br.com.projeto.spring.util.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/remedios")
@RequiredArgsConstructor
public class RemedioController {

    private final RemedioService service;

    /**
     * Busca um remédio pelo seu ID.
     *
     * @param id ID do remédio.
     * @return ResponseEntity contendo o DTO de resposta do remédio.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('remedio:read')")
    public ResponseEntity<RemedioResponse> buscarRemedioPorId(

            @PathVariable
            String id) {

        RemedioResponse remedioResponse = service.buscarRemedioPorId(id);
        return ResponseEntity.ok(remedioResponse);
    }

    /**
     * Lista todos os remédios com paginação.
     *
     * @param pageSize Tamanho da página.
     * @param page Número da página.
     * @return ResponseEntity contendo a página de DTOs de resposta de remédio.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('remedio:read')")
    public ResponseEntity<PageResponse<RemedioResponse>> listarRemedios(

            @RequestParam(defaultValue = "10")
            String pageSize,

            @RequestParam(defaultValue = "0")
            String page) {

        Pageable paginacao = Util.gerarPaginacao(page, pageSize, Sort.by("createdAt"));

        PageResponse<RemedioResponse> remedioPage = service.listarRemedios(paginacao);

        if (Util.vazio(remedioPage.content())) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(remedioPage);
    }

    /**
     * Cria um novo remédio.
     *
     * @param request DTO de requisição para criação do remédio.
     * @return ResponseEntity com o DTO do remédio criado.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('remedio:create')")
    public ResponseEntity<RemedioResponse> cadastrarRemedio(

            @RequestBody
            @Valid
            RemedioRequest request) {

        RemedioResponse response = service.cadastrarRemedio(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Cria vários remédios em lote.
     *
     * @param request Lista de DTOs de requisição para criação dos remédios.
     * @return ResponseEntity com a lista de DTOs dos remédios criados.
     */
    @PostMapping("/lote")
    @PreAuthorize("hasAuthority('remedio:create')")
    public ResponseEntity<List<RemedioResponse>> cadastrarRemediosEmLote(

            @RequestBody
            @Valid
            List<RemedioRequest> request) {

        List<RemedioResponse> remediosCriadosResponse = service.cadastrarRemediosEmLote(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(remediosCriadosResponse);
    }

    /**
     * Atualiza um remédio existente.
     *
     * @param id ID do remédio a ser atualizado.
     * @param request DTO de requisição com os dados para atualização.
     * @return ResponseEntity com o DTO do remédio atualizado.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('remedio:update')")
    public ResponseEntity<RemedioResponse> atualizarRemedio(

            @PathVariable
            String id,

            @RequestBody
            RemedioRequest request) {

        RemedioResponse remedioResponse = service.atualizarRemedio(id, request);

        return ResponseEntity.ok().body(remedioResponse);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('remedio:delete')")
    public ResponseEntity<Void> deletarRemedio(

            @PathVariable
            String id) {

        service.deletarRemedio(id);
        return ResponseEntity.ok().build();
    }
}
