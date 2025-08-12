package br.com.projeto.spring.domain.model;

import java.util.Set;

import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grupo_usuario")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GrupoUsuario extends BaseEntity {

    @Column(nullable = false, unique = true)
    @NotBlank(message = ValidationMessagesKeys.GENERICO_OBRIGATORIO)
    private String nome;

    private String descricao;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "grupo_usuario_permissao", joinColumns = @JoinColumn(name = "grupo_usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id"))
    private Set<Permissao> permissoes;
}
