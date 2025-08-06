package br.com.projeto.spring.exception.messages;

public class ValidationMessagesKeys {

    // Mensagens específicas para remédio
    // Padrão da key: [entidade].[campo].[tipo-validacao]
    public static final String REMEDIO_NOME_OBRIGATORIO = "remedio.nome.obrigatorio";
    public static final String REMEDIO_PRECO_POSITIVO = "remedio.preco.positivo";
    public static final String REMEDIO_QUANTIDADE_POSITIVA_OU_ZERO = "remedio.quantidade.positiva_ou_zero";
    public static final String REMEDIO_VALIDADE_FUTURA = "remedio.validade.futura";
    public static final String REMEDIO_ID_LABORATORIO_UUID = "remedio.id_laboratorio.uuid";
    public static final String REMEDIO_NAO_ENCONTRADO = "remedio.nao.encontrado";

    public static final String LABORATORIO_NAO_ENCONTRADO = "laboratorio.nao.encontrado";
    public static final String LABORATORIO_EMAIL_UNICO = "laboratorio.email.unico";
    public static final String LABORATORIO_EXCLUSAO_NAO_ENCONTRADO = "laboratorio.exclusao.nao.encontrado";
    public static final String LABORATORIO_EXCLUSAO_REMEDIOS_EXISTENTES = "laboratorio.exclusao.remedios.existentes";

    public static final String USUARIO_NAO_ENCONTRADO = "usuario.nao.encontrado";

    // Mensagens genéricas parametrizáveis
    // Padrão da key: [entidade].[tipo-validacao]
    public static final String GENERICO_OBRIGATORIO = "validacao.obrigatorio";
    public static final String GENERICO_TAMANHO_MAXIMO = "validacao.tamanho.maximo";
    public static final String GENERICO_POSITIVO = "validacao.positivo";
    public static final String GENERICO_POSITIVO_OU_ZERO = "validacao.positivo_ou_zero";
    public static final String GENERICO_EMAIL_INVALIDO = "validacao.email.invalido";
    public static final String GENERICO_TELEFONE_INVALIDO = "validacao.telefone.invalido";
    public static final String GENERICO_UUID_PATTERN = "validacao.uuid.pattern";
    public static final String GENERICO_CPF_INVALIDO = "validacao.cpf.invalido";

    // Outras mensagens estáticas
    public static final String ERRO_VALIDACAO = "erro.validacao";
    public static final String ERRO_INTERNO_INESPERADO = "erro.interno.inesperado";

    public static final String AUTENTICACAO_FALHA = "autenticacao.falha";
    public static final String AUTENTICACAO_REFRESH_TOKEN_INVALIDO = "autenticacao.refresh_token.invalido";

}
