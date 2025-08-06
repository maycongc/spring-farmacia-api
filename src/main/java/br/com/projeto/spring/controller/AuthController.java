package br.com.projeto.spring.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.response.TokenResponse;
import br.com.projeto.spring.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(

            @RequestBody
            @Valid
            LoginRequest request) {

        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.senha()));

            String accessToken = jwtUtil.generateToken(request.username());
            String refreshToken = jwtUtil.generateRefreshToken(request.username());

            TokenResponse response = new TokenResponse(accessToken, refreshToken, "Bearer");
            ResponseCookie cookie = buildRefreshTokenCookie(refreshToken);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(

            @CookieValue("refreshToken")
            String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken) || !jwtUtil.isRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = jwtUtil.getUsernameFromToken(refreshToken);
        String newAccessToken = jwtUtil.generateToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        TokenResponse response = new TokenResponse(newAccessToken, newRefreshToken, "Bearer");
        ResponseCookie cookie = buildRefreshTokenCookie(newRefreshToken);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Para JWT, o logout é feito no frontend (remover token local).
        // Se quiser implementar blacklist, salve o token em uma lista de tokens inválidos.
        return ResponseEntity.noContent().build();
    }

    private ResponseCookie buildRefreshTokenCookie(String refreshToken) {
        int validade = 7 * 24 * 60 * 60; // 7 dias em segundos

        ResponseCookie responseCookie = ResponseCookie.from("refreshToken", refreshToken).httpOnly(true).secure(true)
                .path("/api/auth").maxAge(validade).sameSite("Strict").build();

        return responseCookie;
    }
}
