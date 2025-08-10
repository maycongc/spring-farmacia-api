package br.com.projeto.spring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projeto.spring.domain.model.Remedio;

/**
 * Repositório JPA para a entidade Remedio. Fornece métodos para operações de persistência e
 * consultas customizadas.
 */
public interface RemedioRepository extends JpaRepository<Remedio, Long> {

    /**
     * Busca página de remédios pelo id do laboratório.
     * 
     * @param idLaboratorio identificador do laboratório
     * @param paginacao informações de paginação e ordenação
     * @return página de remédios
     */
    Page<Remedio> findByLaboratorioId(Long idLaboratorio, Pageable paginacao);

}
