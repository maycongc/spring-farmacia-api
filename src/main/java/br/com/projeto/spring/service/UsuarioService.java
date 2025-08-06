package br.com.projeto.spring.service;

import org.springframework.data.domain.Pageable;

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;

public interface UsuarioService {

    UsuarioResponse cadastrarUsuario(UsuarioRequest request);

    UsuarioResponse buscarUsuarioPorId(String id) throws ResourceNotFoundException;

    PageResponse<UsuarioResponse> listarUsuarios(Pageable paginacao);

    UsuarioResponse atualizarUsuario(String id, UsuarioUpdateRequest request)
            throws ResourceNotFoundException, IllegalArgumentException;

    void deletarUsuario(String id) throws ResourceNotFoundException, EntityInUseException;

}
