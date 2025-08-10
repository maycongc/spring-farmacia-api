package br.com.projeto.spring.i18n;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import br.com.projeto.spring.util.Util;

@Component
public class MessageResolverImpl implements MessageResolver {

    private final MessageSource messageSource;

    public MessageResolverImpl(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public String get(String key, Object... args) {

        if (!Util.preenchido(key))
            return key;

        Locale locale = LocaleContextHolder.getLocale();

        try {
            return messageSource.getMessage(key, args, locale);
        } catch (NoSuchMessageException e) {
            return key;
        }
    }
}
