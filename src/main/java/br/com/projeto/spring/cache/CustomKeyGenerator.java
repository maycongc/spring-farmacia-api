package br.com.projeto.spring.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import java.lang.reflect.Method;
import java.util.Arrays;

@Component("customKeyGenerator")
public class CustomKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        // Gera chave: nome do método + parâmetros serializados
        return method.getName() + ":" + Arrays.deepToString(params);
    }
}
