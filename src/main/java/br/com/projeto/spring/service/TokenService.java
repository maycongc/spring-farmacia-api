package br.com.projeto.spring.service;

public interface TokenService {

    String createRefreshToken(String username);

    String validateAndGetUsername(String token);

    void revokeRefreshToken(String token);

    String rotateRefreshToken(String oldToken, String userName);

}
