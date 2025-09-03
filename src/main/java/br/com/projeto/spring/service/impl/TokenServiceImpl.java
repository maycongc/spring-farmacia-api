package br.com.projeto.spring.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.projeto.spring.domain.model.RefreshToken;
import br.com.projeto.spring.repository.RefreshTokenRepository;
import br.com.projeto.spring.service.TokenService;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.util.UtilToken;
import br.com.projeto.spring.validation.TokenValidator;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository repository;
    private final UtilToken utilToken;
    private final TokenValidator validator;

    @Override
    @Transactional
    public String createRefreshToken(String username, long ttlSeconds, String ipAddress, String userAgent) {

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

        RefreshToken token = validator.validateRefresh(rawToken);

        token.setLastUsedAt(Instant.now());
        repository.save(token);

        return token;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String rawToken) throws AuthenticationException {

        RefreshToken token = validator.validateRevoke(rawToken);

        if (Util.vazio(token)) {
            return;
        }

        token.setRevoked(true);
        repository.save(token);
        return;
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
        var token = validator.validategetRemainingTime(rawToken);
        return getRemainingSeconds(token);
    }

    @Override
    public Long getRemainingSeconds(RefreshToken token) throws AuthenticationException {
        return token.getExpiresAt().getEpochSecond() - Instant.now().getEpochSecond();
    }
}
