package br.com.projeto.spring.domain.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_grupoUsuario", joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "grupoUsuario_id"))
    private Set<GrupoUsuario> gruposUsuario;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_permissao", joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "permissao_id"))
    private Set<Permissao> permissoes;

}
