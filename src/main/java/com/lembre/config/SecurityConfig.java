package com.lembre.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * HTTP Basic com usuários em memória e dois perfis:
 *  - ADMIN  (pai/mãe): pode tudo;
 *  - MEMBRO (filhos...): pode consultar e concluir atividades.
 * Sem credenciais -> 401. Credenciais válidas sem permissão -> 403.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        // Qualquer usuário autenticado pode concluir atividades e consultar
                        .requestMatchers(HttpMethod.POST, "/atividades/*/concluir").hasAnyRole("ADMIN", "MEMBRO")
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "MEMBRO")
                        // Criar, alterar, excluir e conceder recompensas: só ADMIN
                        .anyRequest().hasRole("ADMIN"));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Usuários de TESTE (somente para a atividade). */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var admin = User.withUsername("pai")
                .password(encoder.encode("admin123"))
                .roles("ADMIN")
                .build();
        var membro = User.withUsername("filho")
                .password(encoder.encode("membro123"))
                .roles("MEMBRO")
                .build();
        return new InMemoryUserDetailsManager(admin, membro);
    }
}
