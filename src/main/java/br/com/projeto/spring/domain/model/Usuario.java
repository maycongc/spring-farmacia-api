package br.com.projeto.spring.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String dataNascimento;

    private String telefone;
    private String cep;
    private String endereco;
    private String complemento;
    private String cidade;
    private String uf;

    private boolean isAdmin;

}
