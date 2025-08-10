package br.com.projeto.spring.security;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final MessageResolver messages;

    public JwtAuthenticationEntryPoint(MessageResolver messages) {
        this.messages = messages;
    }

    @Override
    public void commence(

            HttpServletRequest request,

            HttpServletResponse response,

            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        String mensage = authException.getMessage();

        String errorMessage = messages.get(ValidationMessagesKeys.AUTENTICACAO_NAO_AUTORIZADO);

        String json = String.format("{ \"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\" }",
                LocalDateTime.now(), HttpServletResponse.SC_UNAUTHORIZED, errorMessage, mensage);

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
