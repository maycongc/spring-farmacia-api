package br.com.projeto.spring.exception.handler;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

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
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import br.com.projeto.spring.exception.EntityInUseException;
import br.com.projeto.spring.exception.ResourceNotFoundException;
import br.com.projeto.spring.exception.ValidationException;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.util.Util;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException ex) {

        String mensagemErro = Util.resolveMensagem(ex.getMessage());

        return buildErrorResponse(HttpStatus.NOT_FOUND, mensagemErro, null);
    }

    @ExceptionHandler(EntityInUseException.class)
    public ResponseEntity<Object> handleEntityInUseException(EntityInUseException ex) {

        String mensagemErro = Util.resolveMensagem(ex.getMessage());

        return buildErrorResponse(HttpStatus.CONFLICT, mensagemErro, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
        String mensagemErro = Util.resolveMensagem(ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, mensagemErro, null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        String mensagemErro = Util.resolveMensagem(ex.getMessage());
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

            String resolvedMessage = Util.resolveMensagem(key, argumentos);

            String fieldName = (error instanceof FieldError fe) ? fe.getField() : error.getObjectName();

            errors.merge(fieldName, resolvedMessage, (oldVal, newVal) -> oldVal + ", " + newVal);
        });

        String mensagemErro = Util.resolveMensagem(ValidationMessagesKeys.ERRO_VALIDACAO);

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

        ex.getAllValidationResults().forEach(validationResult -> {

            validationResult.getResolvableErrors().forEach(error -> {
                // Extrai o nome do campo
                String fieldName;

                if (Util.preenchido(error.getCodes()) && Util.preenchido(error.getCodes()[0])) {

                    fieldName = error.getCodes()[0];

                } else if (error instanceof ObjectError objectError) {
                    fieldName = objectError.getObjectName();

                } else {
                    fieldName = "unknown";
                }

                // Extrai o índice se for lista
                String idx = "";
                Object idxObj = Util.preenchido(error.getArguments()) ? error.getArguments()[0] : null;

                if (idxObj != null && idxObj.toString().matches("\\d+")) {
                    idx = "[" + idxObj + "].";
                }

                // Extrai o nome real do campo (última parte do código)
                String[] parts = fieldName.split("\\.");
                String realField = parts[parts.length - 1];

                String resolvedMessage = Util.resolveMensagem(error.getDefaultMessage(), error.getArguments());

                errors.merge(idx + realField, resolvedMessage, (oldVal, newVal) -> oldVal + ", " + newVal);
            });
        });

        String mensagemErro = Util.resolveMensagem(ValidationMessagesKeys.ERRO_VALIDACAO);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensagemErro, errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllUncaught(Exception ex) {
        String message = Util.resolveMensagem(ValidationMessagesKeys.ERRO_INTERNO_INESPERADO);

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

        String mensagemErro = Util.resolveMensagem(ValidationMessagesKeys.ERRO_VALIDACAO);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, mensagemErro, null);
    }

    private ResponseEntity<Object> buildErrorResponse(HttpStatus status, String message, Map<String, String> errors) {

        Map<String, Object> body = new LinkedHashMap<>();

        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);

        if (Util.preenchido(errors)) {
            body.put("fields", errors);
        }

        return ResponseEntity.status(status).body(body);
    }
}
