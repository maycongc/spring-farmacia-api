package br.com.projeto.spring.exception.messages;

public class ValidationMessagesKeys {

    // Mensagens específicas para remédio
    // Padrão da key: [entidade].[campo].[tipo-validacao]
    public static final String REMEDIO_QUANTIDADE_POSITIVA_OU_ZERO = "remedio.quantidade.positiva_ou_zero";
    public static final String REMEDIO_VALIDADE_FUTURA = "remedio.validade.futura";
    public static final String REMEDIO_NAO_ENCONTRADO = "remedio.nao.encontrado";

    public static final String LABORATORIO_NAO_ENCONTRADO = "laboratorio.nao.encontrado";
    public static final String LABORATORIO_EMAIL_UNICO = "laboratorio.email.unico";
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
    public static final String GENERICO_CPF_INVALIDO = "validacao.cpf.invalido";
    public static final String GENERICO_UF_INVALIDO = "validacao.uf.invalido";

    // Outras mensagens estáticas
    public static final String ERRO_VALIDACAO = "erro.validacao";
    public static final String ERRO_INTERNO_INESPERADO = "erro.interno.inesperado";

    public static final String AUTENTICACAO_NAO_AUTORIZADO = "autenticacao.nao.autorizado";
    public static final String AUTENTICACAO_FALHA = "autenticacao.falha";
    public static final String AUTENTICACAO_TOKEN_INVALIDO = "autenticacao.token.invalido";
    public static final String AUTENTICACAO_REFRESH_TOKEN_INVALIDO = "autenticacao.refresh_token.invalido";
    public static final String AUTENTICACAO_REFRESH_TOKEN_EXPIRADO = "autenticacao.refresh_token.expirado";
    public static final String AUTENTICACAO_JWT_TOKEN_FALTANDO = "autenticacao.jwt.token.faltando";
    public static final String AUTENTICACAO_PROCESSAMENTO_JWT = "autenticacao.processamento.jwt";
    public static final String AUTENTICACAO_JWT_USUARIO_NAO_HABILITADO = "autenticacao.jwt.usuario.nao.habilitado";

    public static final String AUTORIZACAO_NAO_PODE_CRIAR_ADMIN = "autorizacao.nao.pode.criar.admin";

}
