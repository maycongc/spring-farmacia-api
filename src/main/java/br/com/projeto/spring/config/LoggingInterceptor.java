package br.com.projeto.spring.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Interceptor para logar início e fim das requisições com traceId.
 */
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private static final String START_TIME_ATTR = "_startTime";

    @Override
    public boolean preHandle(@NonNull
    HttpServletRequest request, @NonNull
    HttpServletResponse response, @NonNull
    Object handler) {
        long start = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, start);
        String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID_REQUEST_ATTRIBUTE);
        log.info("[traceId={}] START {} {} from {}", traceId, request.getMethod(), request.getRequestURI(),
                request.getRemoteAddr());
        return true;
    }

    @Override
    public void afterCompletion(@NonNull
    HttpServletRequest request, @NonNull
    HttpServletResponse response, @NonNull
    Object handler, @Nullable
    Exception ex) {
        Object startObj = request.getAttribute(START_TIME_ATTR);
        long duration = -1L;
        if (startObj instanceof Long l) {
            duration = System.currentTimeMillis() - l;
        }
        String traceId = (String) request.getAttribute(TraceIdFilter.TRACE_ID_REQUEST_ATTRIBUTE);
        int status = response.getStatus();
        if (ex != null) {
            log.info("[traceId={}] END   {} {} status={} duration={}ms ex={}", traceId, request.getMethod(),
                    request.getRequestURI(), status, duration, ex.getClass().getSimpleName());
        } else {
            log.info("[traceId={}] END   {} {} status={} duration={}ms", traceId, request.getMethod(),
                    request.getRequestURI(), status, duration);
        }
    }
}
