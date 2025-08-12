package br.com.projeto.spring.domain.dto.response.remedio;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.projeto.spring.domain.model.Via;

public record RemedioResponse(

        Long id,

        String nome,

        Via via,

        String lote,

        LocalDate validade,

        LaboratorioResumoResponse laboratorio,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
