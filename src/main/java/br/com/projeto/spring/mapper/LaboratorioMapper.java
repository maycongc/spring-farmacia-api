package br.com.projeto.spring.mapper;

import org.springframework.stereotype.Component;

import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioRequest;
import br.com.projeto.spring.domain.dto.request.laboratorio.LaboratorioUpdateRequest;
import br.com.projeto.spring.domain.dto.response.laboratorio.LaboratorioResponse;
import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.util.Util;

@Component
public class LaboratorioMapper {

    public Laboratorio toEntity(LaboratorioRequest request) {

        Laboratorio laboratorio = new Laboratorio();

        laboratorio.setNome(request.nome());
        laboratorio.setEndereco(request.endereco());
        laboratorio.setTelefone(Util.padronizarTelefone(request.telefone()));
        laboratorio.setEmail(request.email());

        return laboratorio;
    }

    public LaboratorioResponse toResponse(Laboratorio laboratorio) {
        return new LaboratorioResponse(

                laboratorio.getId(),

                laboratorio.getNome(),

                laboratorio.getEndereco(),

                laboratorio.getTelefone(),

                laboratorio.getEmail(),

                laboratorio.getCreatedAt(),

                laboratorio.getUpdatedAt()

        );
    }

    public void updateEntity(Laboratorio laboratorio, LaboratorioUpdateRequest request) {

        if (Util.preenchido(request.nome())) {
            laboratorio.setNome(request.nome());
        }

        if (Util.preenchido(request.endereco())) {
            laboratorio.setEndereco(request.endereco());
        }

        if (Util.preenchido(request.telefone())) {
            laboratorio.setTelefone(Util.padronizarTelefone(request.telefone()));
        }
    }
}
