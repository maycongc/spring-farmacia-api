package br.com.projeto.spring.config;

import br.com.projeto.spring.repository.UsuarioRepository;
import br.com.projeto.spring.domain.model.Usuario;
import br.com.projeto.spring.exception.messages.ValidationMessagesKeys;
import br.com.projeto.spring.security.JwtAuthenticationFilter;
import br.com.projeto.spring.security.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuração de segurança da aplicação. Define beans relacionados à segurança, como o
 * PasswordEncoder utilizando Argon2, garantindo que as senhas sejam armazenadas de forma segura
 * conforme as melhores práticas.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

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

            return User.builder().username(usuario.getUsername()).password(usuario.getSenha())
                    .roles(usuario.isAdmin() ? "ADMIN" : "USER").build();
        };
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
            throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth/**", "/public/**").permitAll().anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
