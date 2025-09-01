package br.com.projeto.spring.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.request.auth.RegisterRequest;
import br.com.projeto.spring.domain.dto.response.auth.AuthDataResponse;
import br.com.projeto.spring.domain.dto.response.auth.AuthResponse;
import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.dto.response.auth.RegisterResponse;
import br.com.projeto.spring.service.AuthService;
import br.com.projeto.spring.util.UtilCookie;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody
    @Valid
    RegisterRequest request) {

        RegisterResponse response = service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDataResponse> login(

            @RequestBody
            @Valid
            LoginRequest request,

            HttpServletRequest req) {

        AuthResponse response = service.login(request, req);

        ResponseCookie cookie = UtilCookie.gerarCookieLogin(response.refreshToken(), request.rememberMe());
        AuthDataResponse loginResponse =
                new AuthDataResponse(response.accessToken(), response.tipo(), response.usuario());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(loginResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthDataResponse> refresh(

            @CookieValue("refreshToken")
            Cookie refreshTokenCookie) {

        AuthResponse response = service.refreshToken(refreshTokenCookie);

        AuthDataResponse refreshResponse =
                new AuthDataResponse(response.accessToken(), response.tipo(), response.usuario());

        return ResponseEntity.ok().body(refreshResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthUsuarioResponse> obterUsuarioAtual() {

        AuthUsuarioResponse response = service.obterUsuarioAtual();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(

            @CookieValue("refreshToken")
            String refreshToken) {

        service.logout(refreshToken);
        ResponseCookie cookie = UtilCookie.gerarCookieLogout(refreshToken);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).build();
    }
}
