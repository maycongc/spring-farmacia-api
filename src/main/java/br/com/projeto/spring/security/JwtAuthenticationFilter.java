package br.com.projeto.spring.security;

import java.io.IOException;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.util.JwtUtil;
import br.com.projeto.spring.util.Util;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final MessageResolver messages;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService, MessageResolver messages) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.messages = messages;
    }

    @Override
    protected void doFilterInternal(

            @NonNull
            HttpServletRequest request,

            @NonNull
            HttpServletResponse response,

            @NonNull
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // Ignora endpoints públicos e POST /usuarios
        if (path.startsWith("/auth/login") || path.startsWith("/auth/register") || path.startsWith("/auth/refresh")
                || path.startsWith("/auth/forgot-password") || path.startsWith("/public")
                || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String accessToken = getJwtFromRequest(request);

            if (Util.vazio(accessToken)) {
                SecurityContextHolder.clearContext();
                sendUnauthorizedResponse(response, ValidationMessagesKeys.AUTENTICACAO_JWT_TOKEN_FALTANDO);
                return;
            }

            if (!jwtUtil.validateAccessToken(accessToken)) {
                SecurityContextHolder.clearContext();
                sendUnauthorizedResponse(response, ValidationMessagesKeys.AUTENTICACAO_TOKEN_INVALIDO);
                return;
            }

            String username = jwtUtil.getUsernameFromAccessToken(accessToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (userDetails == null || !userDetails.isEnabled()) {
                SecurityContextHolder.clearContext();
                sendUnauthorizedResponse(response, ValidationMessagesKeys.AUTENTICACAO_JWT_USUARIO_NAO_HABILITADO);
                return;
            }

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            sendUnauthorizedResponse(response, ValidationMessagesKeys.AUTENTICACAO_PROCESSAMENTO_JWT);
        }
    }

    private void sendUnauthorizedResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json; charset=UTF-8");

        String errorMessage = messages.get(ValidationMessagesKeys.AUTENTICACAO_NAO_AUTORIZADO);

        String json = String.format("{ \"timestamp\": \"%s\", \"status\": %d, \"error\": \"%s\", \"message\": \"%s\" }",
                java.time.LocalDateTime.now(), HttpServletResponse.SC_UNAUTHORIZED, errorMessage,
                messages.get(message));

        response.getWriter().write(json);
        response.getWriter().flush();
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
