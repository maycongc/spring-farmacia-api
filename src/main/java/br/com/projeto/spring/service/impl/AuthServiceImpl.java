package br.com.projeto.spring.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.response.TokenResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.mapper.UsuarioMapper;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.security.JwtUtil;
import br.com.projeto.spring.service.AuthService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public TokenResponse login(LoginRequest request) throws AuthenticationException {

        String username = request.username();
        String senha = request.senha();

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, senha));

        String accessToken = jwtUtil.generateToken(username);
        String refreshToken = jwtUtil.generateRefreshToken(username);

        TokenResponse response = new TokenResponse(accessToken, refreshToken, "Bearer");

        return response;
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) throws AuthenticationException {

        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            throw new AuthenticationException("Refresh token inválido") {};
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        TokenResponse response = new TokenResponse(newAccessToken, newRefreshToken, "Bearer");

        return response;
    }

    @Override
    public UsuarioResponse obterUsuarioAtual() throws ResourceNotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO) {});

        return usuarioMapper.toResponse(usuario);
    }

}
