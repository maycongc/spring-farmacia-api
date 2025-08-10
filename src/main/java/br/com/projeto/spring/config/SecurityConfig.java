package br.com.projeto.spring.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.security.JwtAuthenticationEntryPoint;
import br.com.projeto.spring.security.JwtAuthenticationFilter;
import br.com.projeto.spring.security.JwtUtil;
import br.com.projeto.spring.i18n.MessageResolver;
import br.com.projeto.spring.util.Util;

/**
 * Configuração de segurança da aplicação. Define beans relacionados à segurança, como o
 * PasswordEncoder utilizando Argon2, garantindo que as senhas sejam armazenadas de forma segura
 * conforme as melhores práticas.
 */
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    SecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    /**
     * Bean responsável por fornecer um PasswordEncoder baseado em Argon2.
     * 
     * @return uma instância de PasswordEncoder configurada com Argon2
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> {

            Usuario usuario = usuarioRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException(ValidationMessagesKeys.USUARIO_NAO_ENCONTRADO));

            Set<String> authorities = new HashSet<>();

            // Permissões dos grupos
            if (Util.preenchido(usuario.getGruposUsuario())) {

                usuario.getGruposUsuario().forEach(grupo -> {

                    if (Util.preenchido(grupo.getPermissoes())) {
                        grupo.getPermissoes().forEach(p -> authorities.add(p.getKey()));
                    }
                });
            }

            // Permissões individuais
            if (Util.preenchido(usuario.getPermissoes())) {
                usuario.getPermissoes().forEach(p -> authorities.add(p.getKey()));
            }

            return User.builder().username(usuario.getUsername()).password(usuario.getSenha())
                    .authorities(authorities.stream().map(SimpleGrantedAuthority::new).toList()).build();
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService,
            MessageResolver messages) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService, messages);
    }

    @Bean
    public SecurityFilterChain filterChain(

            HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .authorizeHttpRequests(auth -> auth

                        // libera acesso a criação de usuários
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // libera acesso a autenticação e endpoints públicos
                        .requestMatchers("/auth/**", "/public/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
