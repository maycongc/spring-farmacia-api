package br.com.projeto.spring.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.spring.domain.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Busca um usuário pelo nome de usuário.
     *
     * @param username nome de usuário a ser buscado
     * @return Usuário correspondente ou null se não encontrado
     */
    Optional<Usuario> findByUsername(String username);

}
