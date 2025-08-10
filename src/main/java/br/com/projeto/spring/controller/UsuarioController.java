package br.com.projeto.spring.controller;

import org.springframework.data.domain.Pageable;
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

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.service.UsuarioService;
import br.com.projeto.spring.util.Util;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(

            @RequestBody
            @Valid
            UsuarioRequest request) {

        UsuarioResponse response = service.cadastrarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('usuario:read')")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(

            @PathVariable
            Long id) {

        return ResponseEntity.ok(service.buscarUsuarioPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('usuario:read')")
    public ResponseEntity<PageResponse<UsuarioResponse>> listarUsuarios(

            @RequestParam(defaultValue = "0")
            String page,

            @RequestParam(defaultValue = "10")
            String pageSize) {

        Pageable paginacao = Util.gerarPaginacao(page, pageSize);
        PageResponse<UsuarioResponse> response = service.listarUsuarios(paginacao);

        if (Util.vazio(response.content())) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('usuario:update') or @usuarioSecurity.canSelfUpdate(#id)")
    public ResponseEntity<UsuarioResponse> atualizarUsuario(

            @PathVariable
            Long id,

            @RequestBody
            @Valid
            UsuarioUpdateRequest request) {

        UsuarioResponse response = service.atualizarUsuario(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('usuario:delete')")
    public ResponseEntity<Void> deletarUsuario(@PathVariable
    Long id) {
        service.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // @PatchMapping("/{id}/permissoes")
    // @PreAuthorize("hasAuthority('usuario:update') and hasAuthority('permissao:manage')")
    // public ResponseEntity<UsuarioResponse> atualizarPermissoesUsuario(

    // @PathVariable
    // Long id,

    // @RequestBody
    // @Valid
    // List<Long> request) {

    // UsuarioResponse response = service.atualizarUsuario(id, request);
    // return ResponseEntity.ok(response);
    // }
}
