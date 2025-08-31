package br.com.projeto.spring.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.security.JwtUtil;
import br.com.projeto.spring.service.TokenService;
import br.com.projeto.spring.util.Util;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final JwtUtil jwtUtil;
    private final MessageResolver messages;
    private final Map<String, String> refreshStore = new ConcurrentHashMap<>();

    @Override
    public String createRefreshToken(String username) {
        String refreshToken = jwtUtil.generateRefreshToken(username);
        refreshStore.put(refreshToken, username);

        return refreshToken;
    }

    @Override
    public String validateAndGetUsername(String token) throws AuthenticationException {

        String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);

        if (!jwtUtil.validateToken(token) || !jwtUtil.isRefreshToken(token)) {
            throw new AuthenticationException(msgErro) {};
        }

        String username = refreshStore.get(token);

        if (Util.vazio(username))
            throw new AuthenticationException(msgErro) {};

        return username;
    }

    @Override
    public void revokeRefreshToken(String token) {
        refreshStore.remove(token);
    }

    @Override
    public String rotateRefreshToken(String oldToken, String userName) {
        revokeRefreshToken(oldToken);
        return createRefreshToken(userName);
    }

}
