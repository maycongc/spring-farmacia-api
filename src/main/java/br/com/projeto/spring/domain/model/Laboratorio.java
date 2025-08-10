package br.com.projeto.spring.domain.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "laboratorio")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Laboratorio extends BaseEntity {

    @NotBlank
    private String nome;

    @NotBlank
    private String endereco;

    @NotBlank
    private String telefone;

    @NotBlank
    @Email
    private String email;

    @OneToMany(mappedBy = "laboratorio")
    private List<Remedio> remedios;

    public Laboratorio(Long id) {
        this.setId(id);
    }
}
