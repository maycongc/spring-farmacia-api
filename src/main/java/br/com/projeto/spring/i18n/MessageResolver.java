package br.com.projeto.spring.i18n;

public interface MessageResolver {
    String get(String key, Object... args);
}
