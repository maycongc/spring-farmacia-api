package br.com.projeto.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.projeto.spring.domain.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca um usuário pelo nome de usuário.
     *
     * @param username nome de usuário a ser buscado
     * @return Usuário correspondente ou null se não encontrado
     */
    Optional<Usuario> findByUsername(String username);

    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.gruposUsuario g LEFT JOIN FETCH u.permissoes WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithPermissoesAndGrupos(

            @Param("username")
            String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByCpf(String cpf);
}
