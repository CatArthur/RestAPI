package com.mssiz.rest;

import com.mssiz.rest.entities.AuditRequest;
import com.mssiz.rest.repositories.AuditRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final AuditRepository auditRepository;
    static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    public SecurityConfig(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        // InMemoryUserDetailsManager
        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("123"))
                .roles("ADMIN")
                .build();
        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123"))
                .roles("USERS")
                .build();
        UserDetails album = User.withUsername("album")
                .password(encoder.encode("123"))
                .roles("ALBUMS")
                .build();
        UserDetails post = User.withUsername("post")
                .password(encoder.encode("123"))
                .roles("POSTS")
                .build();

        return new InMemoryUserDetailsManager(admin, user, album, post);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.httpBasic()
                .and()
                .csrf().disable()
                .authorizeHttpRequests((authz) -> authz
                    .requestMatchers("/api/users/**").hasAnyRole("ADMIN","USERS")
                    .requestMatchers("/api/albums/**").hasAnyRole("ADMIN","ALBUMS")
                    .requestMatchers("/api/posts/**").hasAnyRole("ADMIN","POSTS")
                    .anyRequest().authenticated()
                )
                .formLogin()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String user = auth == null ? null : auth.getName();
            String body = request.getReader().lines().collect(Collectors.joining());
            auditRepository.save(
                    new AuditRequest(
                            user,
                            request.getRequestURL().toString(),
                            body,
                            HttpMethod.valueOf(request.getMethod()),
                            false));
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(403);
            response.getWriter().write(String.format(
                            """
                               {
                                   "timestamp": "%s",
                                   "status": 403,
                                   "error": "Forbidden",
                               }""",
                            LocalDateTime.now()
                    )
            );
        };
    }

}
