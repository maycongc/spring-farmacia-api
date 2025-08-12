package br.com.projeto.spring.mapper;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.dto.request.remedio.RemedioRequest;
import br.com.projeto.spring.domain.dto.response.remedio.LaboratorioResumoResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.domain.model.Remedio;
import br.com.projeto.spring.util.Util;

@Component
public class RemedioMapper {

    public Remedio toEntity(RemedioRequest request) {

        Remedio remedio = new Remedio();

        remedio.setNome(request.nome());
        remedio.setVia(request.via());
        remedio.setLote(request.lote());
        remedio.setQuantidade(request.quantidade());
        remedio.setValidade(request.validade());

        Long id = request.laboratorio().id();
        remedio.setLaboratorio(new Laboratorio(id));

        return remedio;
    }

    public RemedioResponse toResponse(Remedio remedio) {
        return new RemedioResponse(

                remedio.getId(),

                remedio.getNome(),

                remedio.getVia(),

                remedio.getLote(),

                remedio.getValidade(),

                new LaboratorioResumoResponse(

                        remedio.getLaboratorio().getId(),

                        remedio.getLaboratorio().getNome()),

                remedio.getCreatedAt(),

                remedio.getUpdatedAt()

        );
    }

    public void updateEntity(Remedio remedio, RemedioRequest request, Laboratorio laboratorio) {
        if (request == null)
            return;

        if (Util.preenchido(request.nome())) {
            remedio.setNome(request.nome());
        }

        if (Util.preenchido(request.via())) {
            remedio.setVia(request.via());
        }

        if (Util.preenchido(request.lote())) {
            remedio.setLote(request.lote());
        }

        if (Util.preenchido(request.quantidade())) {
            remedio.setQuantidade(request.quantidade());
        }

        if (Util.preenchido(request.validade())) {
            remedio.setValidade(request.validade());
        }

        if (Util.preenchido(() -> request.laboratorio().id())) {

            Long laboratorioAtualId = remedio.getLaboratorio().getId();
            Long novoLaboratorioId = request.laboratorio().id();

            // Atualiza o laboratório apenas se o ID for diferente do atual
            if (!novoLaboratorioId.equals(laboratorioAtualId)) {
                remedio.setLaboratorio(laboratorio);
            }
        }
    }
}
