package br.com.projeto.spring.exception.handler;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.projeto.spring.config.TraceIdFilter;
import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.util.Util;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageResolver messages;

    public GlobalExceptionHandler(MessageResolver messages) {
        this.messages = messages;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {

        String mensagemErro = messages.get(ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, mensagemErro, null);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<Object> handleEntityInUseException(EntityInUseException ex) {

        String mensagemErro = messages.get(ex.getMessage());
        Map<String, Set<String>> entityRelations = ex.getEntityRelations();

        return buildErrorResponse(HttpStatus.CONFLICT, mensagemErro, entityRelations);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        String mensagemErro = messages.get(ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, mensagemErro, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        String mensagemErro = messages.get(ex.getMessage());
        return buildErrorResponse(HttpStatus.FORBIDDEN, mensagemErro, null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), ex.getErrors());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(

            @NonNull
            MethodArgumentNotValidException ex,

            @NonNull
            HttpHeaders headers,

            @NonNull
            HttpStatusCode status,

            @NonNull
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {

            String key = error.getDefaultMessage();
            Object[] argumentos = error.getArguments();

            String resolvedMessage = messages.get(key, argumentos);

            String fieldName = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();

            errors.merge(fieldName, resolvedMessage, (oldVal, newVal) -> oldVal + ", " + newVal);
        });

        String mensagemErro = messages.get(ValidationMessagesKeys.ERRO_VALIDACAO);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensagemErro, errors);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(

            @NonNull
            HandlerMethodValidationException ex,

            @NonNull
            HttpHeaders headers,

            @NonNull
            HttpStatusCode status,

            @NonNull
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();

        @SuppressWarnings("removal")
        var results = ex.getAllValidationResults();
        results.forEach(validationResult -> validationResult.getResolvableErrors().forEach(error -> {
            String fieldName;
            String[] codes = error.getCodes();
            if (codes != null && codes.length > 0 && Util.preenchido(codes[0])) {
                fieldName = codes[0];
            } else if (error instanceof ObjectError objectError) {
                fieldName = objectError.getObjectName();
            } else {
                fieldName = "unknown";
            }

            String idx = "";
            Object[] args = error.getArguments();
            Object idxObj = (args != null && args.length > 0) ? args[0] : null;
            if (idxObj != null && idxObj.toString().matches("\\d+")) {
                idx = "[" + idxObj + "].";
            }

            String[] parts = fieldName.split("\\.");
            String realField = parts[parts.length - 1];

            String resolvedMessage = messages.get(error.getDefaultMessage(), error.getArguments());
            errors.merge(idx + realField, resolvedMessage, (oldVal, newVal) -> oldVal + ", " + newVal);
        }));

        String mensagemErro = messages.get(ValidationMessagesKeys.ERRO_VALIDACAO);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensagemErro, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaught(Exception ex) {
        String message = messages.get(ValidationMessagesKeys.ERRO_INTERNO_INESPERADO);

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(

            @NonNull
            HttpMessageNotReadableException ex,

            @NonNull
            HttpHeaders headers,

            @NonNull
            HttpStatusCode status,

            @NonNull
            WebRequest request) {

        String mensagemErro = messages.get(ValidationMessagesKeys.ERRO_VALIDACAO);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensagemErro, null);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message, Map<String, ?> errors) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now(ZoneOffset.UTC));
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        String traceId = "-";
        String path = "";
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (Util.preenchido(attrs)) {

                var req = attrs.getRequest();
                if (Util.preenchido(req)) {
                    Object attr = req.getAttribute(TraceIdFilter.TRACE_ID_REQUEST_ATTRIBUTE);
                    if (attr instanceof String s && Util.preenchido(s)) {
                        traceId = s;
                    }
                    path = req.getRequestURI();
                }
            }
        } catch (Exception ignored) {
        }
        body.put("traceId", traceId);
        body.put("path", path);

        if (Util.preenchido(errors))
            body.put("fields", errors);

        return ResponseEntity.status(status).body(body);
    }
}
