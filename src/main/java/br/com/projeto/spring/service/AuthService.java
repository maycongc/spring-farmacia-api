package br.com.projeto.spring.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.request.auth.RegisterRequest;
import br.com.projeto.spring.domain.dto.response.auth.AuthResponse;
import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.dto.response.auth.RegisterResponse;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    RegisterResponse register(RegisterRequest request) throws ValidationException;

    AuthResponse login(LoginRequest request, HttpServletRequest req) throws AccessDeniedException;

    AuthResponse refreshToken(Cookie refreshTokenCookie) throws AuthenticationException;

    AuthUsuarioResponse obterUsuarioAtual() throws ResourceNotFoundException;

    void logout(String refreshToken);
}
