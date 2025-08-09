package br.com.projeto.spring.domain.model;

import java.time.LocalDate;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "remedio")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Remedio extends BaseEntity {

    @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private String nome;

    @Enumerated(EnumType.STRING)
    @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private Via via;

    @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private String lote;

    @PositiveOrZero(message = ValidationMessagesKeys.REMEDIO_QUANTIDADE_POSITIVA_OU_ZERO)
    @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private int quantidade;

    @Future(message = ValidationMessagesKeys.REMEDIO_VALIDADE_FUTURA)
    private LocalDate validade;

    @ManyToOne
    @JoinColumn(name = "laboratorio_id", nullable = false)
    @NotNull(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private Laboratorio laboratorio;

}
