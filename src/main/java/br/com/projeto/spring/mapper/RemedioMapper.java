package br.com.projeto.spring.mapper;

import java.util.UUID;

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

        UUID uuid = UUID.fromString(request.laboratorio().id());
        remedio.setLaboratorio(new Laboratorio(uuid));

        return remedio;
    }

    public RemedioResponse toResponse(Remedio remedio) {
        return new RemedioResponse(

                remedio.getId().toString(),

                remedio.getNome(),

                remedio.getVia(),

                remedio.getValidade(),

                new LaboratorioResumoResponse(

                        remedio.getLaboratorio().getId().toString(),

                        remedio.getLaboratorio().getNome()),

                remedio.getCreatedAt(),

                remedio.getUpdatedAt()

        );
    }

    public void updateEntity(Remedio remedio, RemedioRequest request, Laboratorio laboratorio) {

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

        if (Util.preenchido(() -> request.laboratorio().id()) && Util.preenchido(laboratorio)) {

            // Atualiza o laboratório apenas se o ID for diferente do atual
            String laboratorioAtualId = remedio.getLaboratorio().getId().toString();
            String novoLaboratorioId = request.laboratorio().id();

            if (!novoLaboratorioId.equals(laboratorioAtualId)) {
                remedio.setLaboratorio(laboratorio);
            }
        }
    }
}
