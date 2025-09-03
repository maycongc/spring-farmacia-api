package br.com.projeto.spring.validation;

import java.time.Instant;
import java.util.Optional;

import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.model.RefreshToken;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.repository.RefreshTokenRepository;
import br.com.projeto.spring.util.Util;
import br.com.projeto.spring.util.UtilToken;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenValidator {

    private final MessageResolver messages;
    private final RefreshTokenRepository repository;
    private final UtilToken utilToken;

    public RefreshToken validateRefresh(String rawToken) throws AuthenticationException {

        validateRawTokenPreenchido(rawToken);

        String hashToken = utilToken.hmacSha512Base64(rawToken);
        var maybe = repository.findByTokenHashAndRevokedFalse(hashToken);

        validateTokenNotRevokedExists(maybe);
        validateTokenExpirado(maybe.get());
        // Adicione outras regras de validação conforme necessário

        return maybe.get();
    }

    public RefreshToken validateRevoke(String rawToken) throws AuthenticationException {

        validateRawTokenPreenchido(rawToken);
        String hashToken = utilToken.hmacSha512Base64(rawToken);

        var maybe = repository.findByTokenHash(hashToken);

        if (maybe.isEmpty() || maybe.get().isRevoked()) {
            return null;
        }

        // Adicione outras regras de validação conforme necessário

        return maybe.get();
    }

    public RefreshToken validategetRemainingTime(String rawToken) {

        validateRawTokenPreenchido(rawToken);

        String hashToken = utilToken.hmacSha512Base64(rawToken);
        var maybe = repository.findByTokenHashAndRevokedFalse(hashToken);

        validateTokenNotRevokedExists(maybe);

        return maybe.get();
    }

    private void validateRawTokenPreenchido(String rawToken) {
        if (Util.vazio(rawToken)) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }
    }

    private void validateTokenNotRevokedExists(Optional<RefreshToken> maybe) throws AuthenticationException {
        if (maybe.isEmpty()) {
            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_INVALIDO);
            throw new AuthenticationException(msgErro) {};
        }
    }

    private void validateTokenExpirado(RefreshToken token) {
        if (token.getExpiresAt().isBefore(Instant.now())) {
            token.setRevoked(true);
            repository.save(token);

            String msgErro = messages.get(ValidationMessagesKeys.AUTENTICACAO_REFRESH_TOKEN_EXPIRADO);
            throw new AuthenticationException(msgErro) {};
        }
    }

}
