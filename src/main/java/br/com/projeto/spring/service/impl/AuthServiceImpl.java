package br.com.projeto.spring.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.response.TokenResponse;
import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.mapper.AuthUsuarioMapper;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.security.JwtUtil;
import br.com.projeto.spring.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthUsuarioMapper mapperAuthUsuario;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final MessageResolver messages;

    @Override
    public TokenResponse login(LoginRequest request) throws AccessDeniedException {

        String username = request.username();
        String senha = request.senha();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, senha));
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException(messages.get(ValidationMessagesKeys.AUTENTICACAO_FALHA)) {};
        }

        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        TokenResponse response = new TokenResponse(accessToken, refreshToken, "Bearer");

        return response;
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) throws AuthenticationException {

        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new AuthenticationException(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO) {};
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        TokenResponse response = new TokenResponse(newAccessToken, newRefreshToken, "Bearer");

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthUsuarioResponse obterUsuarioAtual() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsernameWithPermissoesAndGrupos(username)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO) {});

        List<String> permissoes = getPermissoesUsuario(usuario);

        return mapperAuthUsuario.toResponse(usuario, permissoes);
    }

    // Método utilitário para agregar permissões do usuário e dos grupos
    private static List<String> getPermissoesUsuario(Usuario usuario) {
        Set<String> permissoes = new HashSet<>();
        // Permissões dos grupos
        if (usuario.getGruposUsuario() != null) {
            usuario.getGruposUsuario().forEach(grupo -> {
                if (grupo.getPermissoes() != null) {
                    grupo.getPermissoes().forEach(p -> permissoes.add(p.getKey()));
                }
            });
        }
        // Permissões individuais
        if (usuario.getPermissoes() != null) {
            usuario.getPermissoes().forEach(p -> permissoes.add(p.getKey()));
        }

        return permissoes.stream().toList();
    }

}
