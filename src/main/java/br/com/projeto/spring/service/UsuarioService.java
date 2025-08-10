package br.com.projeto.spring.service;

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

    UsuarioResponse buscarUsuarioPorId(Long id)
            throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    PageResponse<UsuarioResponse> listarUsuarios(Pageable paginacao);

    UsuarioResponse atualizarUsuario(Long id, UsuarioUpdateRequest request)
            throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    void deletarUsuario(Long id) throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;

    // UsuarioResponse atualizarPermissoesUsuario(Long id, List<Long> permissoes) throws
    // ResourceNotFoundException;

    // UsuarioResponse atualizarGruposDeUsuario(Long id, List<Long> grupos) throws
    // ResourceNotFoundException;

    UsuarioResponse atualizarSenhaUsuario(Long id, String novaSenha)
            throws AuthenticationException, AccessDeniedException, ResourceNotFoundException;
}
