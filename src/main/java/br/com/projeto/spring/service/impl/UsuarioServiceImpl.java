package br.com.projeto.spring.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.dto.request.usuario.UsuarioRequest;
import br.com.projeto.spring.domain.dto.request.usuario.UsuarioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.mapper.UsuarioMapper;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.service.UsuarioService;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.validation.UsuarioValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;
    private final UsuarioValidator usuarioValidator;

    @Override
    @Transactional
    public UsuarioResponse cadastrarUsuario(UsuarioRequest request) {

        Usuario usuario = mapper.toEntity(request, passwordEncoder);
        usuarioValidator.validarCadastro(usuario);

        repository.save(usuario);
        return mapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse buscarUsuarioPorId(Long id) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        return mapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UsuarioResponse> listarUsuarios(Pageable paginacao) {
        Page<Usuario> page = repository.findAll(paginacao);
        return Util.toPageResponse(page, mapper::toResponse);
    }

    @Override
    @Transactional
    public UsuarioResponse atualizarUsuario(Long id, UsuarioUpdateRequest request) {

        Usuario usuarioBanco = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        Usuario usuarioAtualizado = mapper.copy(usuarioBanco);
        mapper.updateEntity(usuarioAtualizado, request);

        usuarioValidator.validarAtualizacao(usuarioAtualizado);

        repository.save(usuarioAtualizado);

        return mapper.toResponse(usuarioAtualizado);
    }

    @Override
    @Transactional
    public void deletarUsuario(Long id) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        repository.delete(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse atualizarSenhaUsuario(Long id, String novaSenha) {

        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

        usuario.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(usuario);

        return mapper.toResponse(usuario);
    }

}
