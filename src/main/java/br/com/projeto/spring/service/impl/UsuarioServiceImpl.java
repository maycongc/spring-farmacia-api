package br.com.projeto.spring.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.mapper.UsuarioMapper;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.service.UsuarioService;
import br.com.projeto.spring.util.Util;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;

    @Override
    @Transactional
    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {

        Usuario usuario = mapper.toEntity(request, passwordEncoder);

        repository.save(usuario);
        return mapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse buscarUsuarioPorId(String id) throws ResourceNotFoundException {

        UUID uuid = UUID.fromString(id);
        Usuario usuario = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        return mapper.toResponse(usuario);
    }

    @Override
    public PageResponse<UsuarioResponse> listarUsuarios(Pageable paginacao) {
        Page<Usuario> page = repository.findAll(paginacao);
        return Util.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public UsuarioResponse atualizarUsuario(String id, UsuarioUpdateRequest request)
            throws ResourceNotFoundException, IllegalArgumentException {

        UUID uuid = UUID.fromString(id);
        Usuario usuario = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        mapper.updateEntity(usuario, request, passwordEncoder);
        repository.save(usuario);

        return mapper.toResponse(usuario);
    }

    @Override
    @Transactional
    public void deletarUsuario(String id) throws ResourceNotFoundException, EntityInUseException {

        UUID uuid = UUID.fromString(id);
        Usuario usuario = repository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        repository.delete(usuario);
    }

}
