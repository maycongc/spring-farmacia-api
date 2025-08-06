package br.com.projeto.spring.domain.dto.response.remedio;

import java.time.LocalDate;
import java.time.LocalDateTime;

import br.com.projeto.spring.domain.model.Via;

public record RemedioResponse(

        String id,

        String nome,

        Via via,

        LocalDate validade,

        LaboratorioResumoResponse laboratorio,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
