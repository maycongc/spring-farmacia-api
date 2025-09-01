package br.com.projeto.spring.service.impl;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.dto.request.LoginRequest;
import br.com.projeto.spring.domain.dto.request.auth.RegisterRequest;
import br.com.projeto.spring.domain.dto.response.auth.AuthResponse;
import br.com.projeto.spring.domain.dto.response.auth.AuthUsuarioResponse;
import br.com.projeto.spring.domain.dto.response.auth.RegisterResponse;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.mapper.AuthMapper;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.service.AuthService;
import br.com.projeto.spring.service.TokenService;
import br.com.projeto.spring.util.JwtUtil;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.validation.AuthValidator;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthMapper mapperAuth;
    private final AuthenticationManager authenticationManager;

    private final MessageResolver messages;

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;

    private final AuthValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) throws ValidationException {

        Usuario usuario = mapperAuth.toEntityRegister(request, passwordEncoder);
        validator.validarCadastro(usuario);
        usuarioRepository.save(usuario);

        RegisterResponse response = mapperAuth.toResponseRegister(usuario);
        return response;
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest req) throws AccessDeniedException {

        String username = request.username();
        String senha = request.senha();

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, senha));
        } catch (AccessDeniedException e) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_FALHA);
            throw new AccessDeniedException(msgErro) {};
        }

        AuthUsuarioResponse usuario = getUsuarioAutenticado(username);
        String accessToken = jwtUtil.generateAccessToken(username);

        boolean rememberMe = request.rememberMe();
        long ttlSeconds = rememberMe ? Duration.ofDays(15).getSeconds() : Duration.ofHours(12).getSeconds();

        String userIpAddress = extractClientIp(req);
        String userAgent = req.getHeader("User-Agent");
        String userMacAddress = req.getHeader("X-Client-MAC");

        String refreshToken =
                tokenService.createRefreshToken(username, ttlSeconds, userIpAddress, userMacAddress, userAgent);

        AuthResponse response = new AuthResponse(accessToken, refreshToken, "Bearer", usuario);

        return response;
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(Cookie refreshTokenCookie) throws AuthenticationException {

        var rawOldToken = refreshTokenCookie.getValue();
        var oldToken = tokenService.validateAndGetToken(rawOldToken);

        var username = oldToken.getUsername();

        AuthUsuarioResponse usuario = getUsuarioAutenticado(username);
        String newAccessToken = jwtUtil.generateAccessToken(username);

        AuthResponse response = new AuthResponse(newAccessToken, null, "Bearer", usuario);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AuthUsuarioResponse obterUsuarioAtual() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return getUsuarioAutenticado(username);
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        tokenService.revokeRefreshToken(refreshToken);
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

    private AuthUsuarioResponse getUsuarioAutenticado(String username) throws ResourceNotFoundException {
        Usuario usuario = usuarioRepository.findByUsernameWithPermissoesAndGrupos(username)
                .orElseThrow(() -> new ResourceNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO) {});

        List<String> permissoes = getPermissoesUsuario(usuario);

        return mapperAuth.userToResponse(usuario, permissoes);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");

        if (Util.preenchido(xf)) {
            return xf.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
