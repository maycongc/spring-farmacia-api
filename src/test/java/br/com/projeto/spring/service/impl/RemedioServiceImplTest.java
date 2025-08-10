package br.com.projeto.spring.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.projeto.spring.domain.dto.request.EntidadeIdRequest;
import br.com.projeto.spring.domain.dto.request.remedio.RemedioRequest;
import br.com.projeto.spring.domain.dto.response.PageResponse;
import br.com.projeto.spring.domain.dto.response.remedio.LaboratorioResumoResponse;
import br.com.projeto.spring.domain.dto.response.remedio.RemedioResponse;
import br.com.projeto.spring.domain.model.Laboratorio;
import br.com.projeto.spring.domain.model.Remedio;
import br.com.projeto.spring.domain.model.Via;
import br.com.projeto.spring.mapper.RemedioMapper;
import br.com.projeto.spring.repository.LaboratorioRepository;
import br.com.projeto.spring.repository.RemedioRepository;

class RemedioServiceImplTest {

    @Mock
    private RemedioRepository repository;

    @Mock
    private LaboratorioRepository laboratorioRepository;

    @Mock
    private RemedioMapper mapper;

    @InjectMocks
    private RemedioServiceImpl remedioService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private static final Long LAB_ID_FIXO = 11111111L;

    private static Remedio criarRemedioCompleto() {
        Remedio remedio = new Remedio();
        remedio.setId(1L);
        remedio.setNome("Dipirona");
        remedio.setVia(Via.ORAL);
        remedio.setLote("L123");
        remedio.setQuantidade(10);
        remedio.setValidade(LocalDate.now().plusDays(10));
        Laboratorio laboratorio = new Laboratorio();
        laboratorio.setId(LAB_ID_FIXO);
        laboratorio.setNome("LabTest");
        remedio.setLaboratorio(laboratorio);
        return remedio;
    }

    private static RemedioRequest criarRemedioCreateRequest() {
        EntidadeIdRequest laboratorio = new EntidadeIdRequest(LAB_ID_FIXO);
        return new RemedioRequest("Dipirona", Via.ORAL, "L123", 10, LocalDate.now().plusDays(10), laboratorio);
    }

    @Test
    @DisplayName("Deve criar um remédio com sucesso")
    void criarRemedio_DeveRetornarRemedioResponse() {
        RemedioRequest request = criarRemedioCreateRequest();
        Remedio remedioSalvo = criarRemedioCompleto();

        Laboratorio laboratorio = remedioSalvo.getLaboratorio();
        LocalDateTime now = LocalDateTime.now();
        when(laboratorioRepository.findById(laboratorio.getId())).thenReturn(Optional.of(laboratorio));
        when(mapper.toEntity(any(RemedioRequest.class))).thenReturn(remedioSalvo);
        when(repository.save(any(Remedio.class))).thenReturn(remedioSalvo);
        when(mapper.toResponse(any(Remedio.class)))
                .thenReturn(new RemedioResponse(remedioSalvo.getId(), remedioSalvo.getNome(), remedioSalvo.getVia(),
                        remedioSalvo.getValidade(), new LaboratorioResumoResponse(remedioSalvo.getLaboratorio().getId(),
                                remedioSalvo.getLaboratorio().getNome()),
                        now, now));

        RemedioResponse response = remedioService.cadastrarRemedio(request);

        assertThat(response).isNotNull();
        assertThat(response.nome()).isEqualTo(remedioSalvo.getNome());
        assertThat(response.via()).isEqualTo(remedioSalvo.getVia());
        assertThat(response.validade()).isEqualTo(remedioSalvo.getValidade());
        assertThat(response.laboratorio().nome()).isEqualTo(remedioSalvo.getLaboratorio().getNome());
        assertThat(response.createdAt()).isNotNull();
        assertThat(response.updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar PageResponse com lista de RemedioResponse quando houver dados")
    void listarTodosRemedios_DeveRetornarPageResponseComLista() {
        Remedio remedio = criarRemedioCompleto();
        List<Remedio> remedios = List.of(remedio);

        Page<Remedio> page = new PageImpl<>(remedios, Pageable.unpaged(), remedios.size());

        LocalDateTime now = LocalDateTime.now();
        when(repository.findAll(any(Pageable.class))).thenReturn(page);
        when(mapper.toResponse(any(Remedio.class))).thenReturn(new RemedioResponse(remedio.getId(), remedio.getNome(),
                remedio.getVia(), remedio.getValidade(),
                new LaboratorioResumoResponse(remedio.getLaboratorio().getId(), remedio.getLaboratorio().getNome()),
                now, now));

        PageResponse<RemedioResponse> response = remedioService.listarRemedios(Pageable.unpaged());

        assertThat(response).isNotNull();
        assertThat(response.content()).isNotNull();
        assertThat(response.content()).hasSize(1);
        assertThat(response.content().get(0).nome()).isEqualTo("Dipirona");
        assertThat(response.content().get(0).laboratorio().nome()).isEqualTo("LabTest");
        assertThat(response.content().get(0).createdAt()).isNotNull();
        assertThat(response.content().get(0).updatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve retornar PageResponse vazio quando não houver dados")
    void listarTodosRemedios_DeveRetornarPageResponseVazio() {
        Page<Remedio> page = new PageImpl<>(List.of());

        when(repository.findAll(any(Pageable.class))).thenReturn(page);

        PageResponse<RemedioResponse> response = remedioService.listarRemedios(Pageable.unpaged());

        assertThat(response).isNotNull();
        assertThat(response.content()).isEmpty();
    }
}
