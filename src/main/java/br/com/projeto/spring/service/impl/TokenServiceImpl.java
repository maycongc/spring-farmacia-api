package br.com.projeto.spring.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.model.RefreshToken;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.repository.RefreshTokenRepository;
import br.com.projeto.spring.service.TokenService;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.util.UtilToken;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository repository;
    private final UtilToken utilToken;
    private final MessageResolver messages;

    @Override
    @Transactional
    public String createRefreshToken(String username, long ttlSeconds, String ipAddress, String macAddress,
            String userAgent) {

        String rawToken = utilToken.generateRawToken();
        String hashToken = utilToken.hmacSha512Base64(rawToken);

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ttlSeconds);

        RefreshToken token = new RefreshToken();
        token.setTokenHash(hashToken);
        token.setUsername(username);
        token.setCreatedAt(now);
        token.setLastUsedAt(now);
        token.setExpiresAt(expiresAt);
        token.setRevoked(false);
        token.setIpAddress(ipAddress);
        token.setMacAddress(macAddress);
        token.setUserAgent(userAgent);

        try {
            repository.save(token);
        } catch (Exception e) {
            // Em caso de colisão (muito improvável) gerar denovo e salvar novamente
            String rawToken2 = utilToken.generateRawToken();
            String hashToken2 = utilToken.hmacSha512Base64(rawToken2);
            token.setTokenHash(hashToken2);
            repository.save(token);
            return rawToken2;
        }

        return rawToken;
    }

    @Override
    @Transactional
    public RefreshToken validateAndGetToken(String rawToken) throws AuthenticationException {

        if (Util.vazio(rawToken)) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }

        String hashToken = utilToken.hmacSha512Base64(rawToken);
        var maybe = repository.findByTokenHashAndRevokedFalse(hashToken);

        if (maybe.isEmpty()) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }

        RefreshToken token = maybe.get();

        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            repository.save(token);

            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_EXPIRADO);
            throw new AuthenticationException(msgErro) {};
        }

        token.setLastUsedAt(Instant.now());
        repository.save(token);

        return token;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) throws AuthenticationException {

        if (Util.vazio(rawToken)) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }

        String hashToken = utilToken.hmacSha512Base64(rawToken);
        Optional<RefreshToken> maybe = repository.findByTokenHashAndRevokedFalse(hashToken);

        maybe.ifPresent(token -> {
            token.setRevoked(true);
            repository.save(token);
        });
    }

    @Override
    @Transactional
    public void revokeAllForUser(String username) {
        repository.deleteByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefreshToken> findActiveTokensForUser(String username) {
        return repository.findAllByUsernameAndRevokedFalse(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRemainingSeconds(String rawToken) throws AuthenticationException {

        if (Util.vazio(rawToken)) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }

        String hashToken = utilToken.hmacSha512Base64(rawToken);
        var maybe = repository.findByTokenHashAndRevokedFalse(hashToken);

        if (maybe.isEmpty()) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }

        return getRemainingSeconds(maybe.get());
    }

    @Override
    public Long getRemainingSeconds(RefreshToken token) throws AuthenticationException {
        return token.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
    }
}
