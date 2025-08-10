package br.com.projeto.spring.config;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.http.converter.HttpMessageConverter;

import br.com.projeto.spring.util.Util;

/**
 * Adiciona traceId no corpo de respostas de sucesso (2xx) quando body é um Map ou pode ser
 * envolvido.
 */
@ControllerAdvice
@Component
public class TraceIdResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(@NonNull
    MethodParameter returnType, @NonNull
    Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // aplicar genericamente, filtramos dentro
    }

    @Override
    public Object beforeBodyWrite(@Nullable
    Object body, @NonNull
    MethodParameter returnType, @NonNull
    MediaType selectedContentType, @NonNull
    Class<? extends HttpMessageConverter<?>> selectedConverterType, @NonNull
    ServerHttpRequest request, @NonNull
    ServerHttpResponse response) {

        // Não temos acesso direto ao status code aqui; aplicamos heurística por tipo de body
        String traceId = null;
        try {
            Object attr = ((jakarta.servlet.http.HttpServletRequest) request)
                    .getAttribute(TraceIdFilter.TRACE_ID_REQUEST_ATTRIBUTE);
            if (attr instanceof String s && Util.preenchido(s))
                traceId = s;
        } catch (Exception ignored) {
        }

        if (!Util.preenchido(traceId))
            return body; // não encontrado

        if (body instanceof Map<?, ?> map) {
            // Evita sobrescrever caso já exista
            if (!map.containsKey("traceId")) {
                try {
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> editable = (Map<Object, Object>) map;
                    editable.put("traceId", traceId);
                } catch (UnsupportedOperationException e) {
                    // Mapa imutável: criar novo
                    Map<Object, Object> novo = new java.util.LinkedHashMap<>(map);
                    novo.put("traceId", traceId);
                    return novo;
                }
            }
            return body;
        }

        // Não altera outros tipos (evita quebrar contratos de DTOs)
        return body;
    }
}
