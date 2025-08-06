package br.com.projeto.spring.domain.dto.response;

import java.util.List;

public record PageResponse<T>(List<T> content, int pageNumber, int pageSize, long totalElements, int totalPages) {

}
