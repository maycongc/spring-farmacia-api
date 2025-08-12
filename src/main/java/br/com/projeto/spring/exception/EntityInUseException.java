package br.com.projeto.spring.exception;

import java.util.Map;
import java.util.Set;

import lombok.Getter;

@Getter
public class EntityInUseException extends RuntimeException {

    private final Map<String, Set<String>> entityRelations;

    public EntityInUseException(String message) {
        super(message);
        entityRelations = Map.of();
    }

    public EntityInUseException(Map<String, Set<String>> entityRelations) {
        super("Entidade(s) em uso");
        this.entityRelations = entityRelations;
    }
}
