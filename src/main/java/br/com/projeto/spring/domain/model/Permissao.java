package br.com.projeto.spring.domain.model;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.EqualsAndHashCode.Include;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "permissao")
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Permissao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Include
    @PositiveOrZero(message = ValidationMessagesKeys.GENERICO_POSITIVO_OU_ZERO)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    String key;

    @Column(nullable = false)
    @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    String nome;

    String descricao;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    CategoriaPermissao categoria;
}
