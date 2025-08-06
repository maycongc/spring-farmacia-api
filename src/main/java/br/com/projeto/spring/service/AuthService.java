package br.com.projeto.spring.service;

import org.springframework.security.core.AuthenticationException;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.response.TokenResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.exception.ResourceNotFoundException;

public interface AuthService {
    TokenResponse login(LoginRequest request) throws AuthenticationException;

    TokenResponse refreshToken(String refreshToken) throws AuthenticationException;

    UsuarioResponse obterUsuarioAtual() throws ResourceNotFoundException;
}
