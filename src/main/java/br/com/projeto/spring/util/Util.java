package br.com.projeto.spring.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import br.com.projeto.spring.domain.dto.response.PageResponse;

public class Util {

    /**
     * Converte um objeto para Integer, aceitando String ou Integer. Retorna null se não for possível
     * converter.
     *
     * @param value Valor a ser convertido.
     * @return Integer convertido ou null se não for possível.
     */
    public static Integer toInt(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Gera um objeto Pageable a partir de strings de página e tamanho da página.
     *
     * @param page Número da página como String.
     * @param pageSize Tamanho da página como String.
     * @return Pageable configurado.
     */
    public static Pageable gerarPaginacao(String page, String pageSize) {
        Pageable pageable = PageRequest.of(Util.toInt(page), Util.toInt(pageSize));
        return pageable;
    }

    /**
     * Gera um objeto Pageable para paginação, com suporte a ordenação.
     *
     * @param paginaString Número da página como String (começa em 0). Se inválido ou nulo, assume 0.
     * @param tamanhoPaginaString Tamanho da página como String. Se inválido ou nulo, assume 10.
     * @param sortBy Objeto Sort para ordenação. Se nulo, não ordena.
     * @return Pageable configurado conforme os parâmetros.
     */
    public static Pageable gerarPaginacao(String paginaString, String tamanhoPaginaString, Sort sortBy) {
        final int DEFAULT_PAGE = 0;
        final int DEFAULT_SIZE = 10;

        int pagina = Optional.ofNullable(toInt(paginaString)).filter(p -> p >= 0).orElse(DEFAULT_PAGE);

        int tamanhoPagina = Optional.ofNullable(toInt(tamanhoPaginaString)).filter(t -> t > 0).orElse(DEFAULT_SIZE);

        Sort sort = (sortBy != null && sortBy.isSorted()) ? sortBy : Sort.unsorted();

        return PageRequest.of(pagina, tamanhoPagina, sort);
    }

    /**
     * Versão segura para verificação de objetos potencialmente nulos. Utilize para verificar se um
     * Optional está presente, ou se um campo de um objeto aninhado está preenchido: item.getCampo() se
     * o item ou campo forem nulos, não haverá exceção.
     * 
     * @param supplier Fornecedor do objeto
     * @return true se vazio, false caso preenchido
     */
    public static boolean vazio(Supplier<Object> supplier) {
        return !preenchido(supplier);
    }

    /**
     * Verificar se um objeto está vazio conforme seu tipo.
     * 
     * @param objeto Objeto a ser verificado
     * @return true se vazio, false caso preenchido
     */
    public static boolean vazio(Object objeto) {
        return !preenchido(objeto);
    }

    /**
     * Versão segura para verificação de objetos potencialmente nulos. Utilize para verificar se um
     * Optional está presente, ou se um campo de um objeto aninhado está preenchido: item.getCampo() se
     * o item ou campo forem nulos, não haverá exceção.
     * 
     * @param supplier Fornecedor do objeto
     * @return true se preenchido, false caso contrário
     */
    public static boolean preenchido(Supplier<Object> supplier) {
        try {
            return preenchido(supplier.get());
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * Verifica se um objeto está preenchido conforme seu tipo.
     * 
     * @param objeto Objeto a ser verificado
     * @return true se preenchido, false caso contrário
     */
    public static boolean preenchido(Object objeto) {

        if (objeto == null) {
            return false;
        }

        if (objeto instanceof CharSequence) {
            return !((CharSequence) objeto).toString().trim().isEmpty();
        }

        if (objeto instanceof Collection) {
            return !((Collection<?>) objeto).isEmpty();
        }

        if (objeto instanceof Map) {
            return !((Map<?, ?>) objeto).isEmpty();
        }

        if (objeto instanceof Object[]) {
            return ((Object[]) objeto).length > 0;
        }

        if (objeto.getClass().isArray()) {
            return java.lang.reflect.Array.getLength(objeto) > 0;
        }

        if (objeto instanceof Optional) {
            return ((Optional<?>) objeto).isPresent();
        }

        if (objeto instanceof Supplier) {
            return preenchido(((Supplier<?>) objeto).get());
        }

        if (objeto instanceof Boolean) {
            return true; // Boolean sempre é considerado preenchido se não for null
        }

        if (objeto instanceof Number) {
            return true; // Números sempre são considerados preenchidos se não forem null
        }

        return true; // Qualquer outro objeto não-nulo é considerado preenchido
    }

    /**
     * Converte um Page<T> para PageResponse<R> usando um mapper.
     *
     * @param page Page de entidades.
     * @param mapper Função para converter T em R.
     * @return PageResponse<R> com os dados convertidos.
     */
    public static <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {

        int pageNumber = page.getNumber();
        int pageSize = page.getSize();
        long totalElements = page.getTotalElements();
        int totalPages = page.getTotalPages();

        List<R> content =
                Util.preenchido(page.getContent()) ? page.getContent().stream().map(mapper).toList() : List.of();

        return new PageResponse<>(content, pageNumber, pageSize, totalElements, totalPages);
    }

    /**
     * Padroniza um telefone brasileiro para o formato (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX. Aceita
     * entradas com ou sem DDD, parênteses, espaços ou hífen.
     *
     * @param telefone Telefone a ser padronizado.
     * @return Telefone no formato (XX) 9XXXX-XXXX ou (XX) XXXX-XXXX, ou null se não for possível
     *         padronizar.
     */
    public static String padronizarTelefone(String telefone) {
        if (Util.vazio(telefone)) {
            return null;
        }

        // Remove tudo que não for dígito
        String numeros = telefone.replaceAll("\\D", "");

        // Verifica se tem DDD (2 dígitos iniciais)
        if (numeros.length() == 10) { // Ex: 1123456789

            String ddd = numeros.substring(0, 2);
            String parte1 = numeros.substring(2, 6);
            String parte2 = numeros.substring(6);

            return String.format("(%s) %s-%s", ddd, parte1, parte2);

        } else if (numeros.length() == 11) { // Ex: 11912345678
            String ddd = numeros.substring(0, 2);
            String parte1 = numeros.substring(2, 7);
            String parte2 = numeros.substring(7);

            return String.format("(%s) %s-%s", ddd, parte1, parte2);

        } else if (numeros.length() == 8 || numeros.length() == 9) {
            // Sem DDD, apenas número local
            String parte1 = numeros.length() == 8 ? numeros.substring(0, 4) : numeros.substring(0, 5);
            String parte2 = numeros.length() == 8 ? numeros.substring(4) : numeros.substring(5);
            return String.format("%s-%s", parte1, parte2);
        }

        // Não é possível padronizar
        return null;
    }
}
