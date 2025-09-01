package br.com.projeto.spring.util;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UtilToken {

    private static final SecureRandom secureRandom = new SecureRandom();

    private final String pepper;

    public UtilToken(

            @Value("${security.refresh-token.pepper}")
            String pepper

    ) {
        this.pepper = pepper;
    }

    // gera token raw para ser enviado no cookie (base64 url-safe sem padding)
    public String generateRawToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    // calcula HMAC-SHA512 e retorna Base64 url-safe
    public String hmacSha512Base64(String rawToken) {
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec keySpec = new SecretKeySpec(this.pepper.getBytes(StandardCharsets.UTF_8), "HmacSHA512");

            mac.init(keySpec);
            byte[] result = mac.doFinal(rawToken.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA512", e);
        }
    }
}
