package br.com.projeto.spring.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.response.TokenResponse;
import br.com.projeto.spring.domain.dto.response.usuario.UsuarioResponse;
import br.com.projeto.spring.service.AuthService;
import br.com.projeto.spring.util.UtilCookie;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(

            @RequestBody
            @Valid
            LoginRequest request) {

        TokenResponse response = service.login(request);
        ResponseCookie cookie = UtilCookie.gerarCookieRefreshToken(response.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(

            @CookieValue("refreshToken")
            String refreshToken) {

        TokenResponse response = service.refreshToken(refreshToken);
        ResponseCookie cookie = UtilCookie.gerarCookieRefreshToken(response.refreshToken());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> obterUsuarioAtual() {

        UsuarioResponse usuario = service.obterUsuarioAtual();
        return ResponseEntity.ok(usuario);
    }
}
