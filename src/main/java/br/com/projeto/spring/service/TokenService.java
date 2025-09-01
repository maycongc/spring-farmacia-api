package br.com.projeto.spring.service;

import java.util.List;

import org.springframework.security.core.AuthenticationException;

import br.com.projeto.spring.domain.model.RefreshToken;

public interface TokenService {

    String createRefreshToken(String username, long ttlSeconds, String ipAddress, String macAddress, String userAgent);

    RefreshToken validateAndGetToken(String rawToken) throws AuthenticationException;

    void revokeRefreshToken(String rawToken) throws AuthenticationException;

    void revokeAllForUser(String username);

    List<RefreshToken> findActiveTokensForUser(String username);

    Long getRemainingSeconds(String rawToken);

    Long getRemainingSeconds(RefreshToken rawToken);
}
