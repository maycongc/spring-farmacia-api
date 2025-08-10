package br.com.projeto.spring.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

/**
 * Filtro responsável por gerar um traceId por requisição para correlação de logs e retorno em
 * respostas de erro.
 */
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_KEY = "traceId";
    public static final String TRACE_ID_REQUEST_ATTRIBUTE = "TRACE_ID";

    @Override
    protected void doFilterInternal(@NonNull
    HttpServletRequest request, @NonNull
    HttpServletResponse response, @NonNull
    FilterChain filterChain) throws ServletException, IOException {

        String traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID_KEY, traceId);
        request.setAttribute(TRACE_ID_REQUEST_ATTRIBUTE, traceId);
        response.setHeader("X-Trace-Id", traceId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID_KEY);
        }
    }
}
