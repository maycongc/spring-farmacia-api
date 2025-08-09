package br.com.projeto.spring.service;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.exception.ResourceNotFoundException;

public interface UsuarioService {

    UsuarioResponse cadastrarUsuario(UsuarioRequest request) throws AccessDeniedException;

    UsuarioResponse buscarUsuarioPorId(String id)
            throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    PageResponse<UsuarioResponse> listarUsuarios(Pageable paginacao);

    UsuarioResponse atualizarUsuario(String id, UsuarioUpdateRequest request)
            throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    void deletarUsuario(String id) throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    // UsuarioResponse atualizarPermissoesUsuario(String id, List<Long> permissoes) throws
    // ResourceNotFoundException;

    // UsuarioResponse atualizarGruposDeUsuario(String id, List<Long> grupos) throws
    // ResourceNotFoundException;

    // UsuarioResponse atualizarSenhaUsuario(String id, String novaSenha) throws
    // ResourceNotFoundException;
}
