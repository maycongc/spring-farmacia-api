package br.com.projeto.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.spring.domain.model.Laboratorio;

/**
 * Repositório JPA para a entidade Laboratorio. Fornece métodos para operações de persistência e
 * consultas customizadas.
 */
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    /**
     * Busca um laboratório pelo e-mail.
     * 
     * @param email e-mail do laboratório
     * @return Optional contendo o laboratório, se encontrado
     */
    Optional<Laboratorio> findByEmail(String email);

    /**
     * Verifica se existe algum laboratório com o e-mail informado.
     * 
     * @param email e-mail do laboratório
     * @return true se existir, false caso contrário
     */
    boolean existsByEmail(String email);

    /**
     * Verifica se o laboratório possui remédios relacionados.
     * 
     * @param id identificador do laboratório
     * @return true se possuir remédios, false caso contrário
     */
    boolean existsByIdAndRemediosIsNotEmpty(Long id);
}
