package br.com.projeto.spring.domain.dto.response.laboratorio;

import java.time.LocalDateTime;

public record LaboratorioResponse(

        String id,

        String nome,

        String endereco,

        String telefone,

        String email,

        LocalDateTime createdAt,

        LocalDateTime updatedAt) {

}
