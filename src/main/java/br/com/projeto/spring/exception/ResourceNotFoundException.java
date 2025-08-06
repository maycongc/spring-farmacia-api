package br.com.projeto.spring.exception;

/**
 * Exceção lançada quando um recurso não é encontrado no sistema.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String key) {
        super(key);
    }
}
