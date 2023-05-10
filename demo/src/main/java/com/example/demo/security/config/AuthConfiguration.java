package com.example.demo.security.config;

import com.example.demo.security.config.SecurityExpressions.Endpoint;
import com.example.demo.security.config.SecurityExpressions.UserRole;
import com.example.demo.security.jwt.component.JwtRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.security.SecureRandom;
import java.util.List;

@Configuration
@EnableWebSecurity
public class AuthConfiguration{

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10, new SecureRandom());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedHeaders(List.of("Origin", "Access-Control-Allow-Origin", "Content-Type",
            "Accept", "Authorization", "Origin, Accept", "X-Requested-With",
            "Access-Control-Request-Method", "Access-Control-Request-Headers"));
        config.setExposedHeaders(List.of("Origin", "Content-Type", "Accept", "Authorization",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {

        // Private endpoints
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.PUT, Endpoint.USERS_DETAIL.getPattern()).hasAnyRole(UserRole.ADMIN.name(),UserRole.USER.name())
                .requestMatchers(HttpMethod.DELETE, Endpoint.USERS_DETAIL.getPattern()).hasRole(UserRole.ADMIN.name())
                .requestMatchers(HttpMethod.GET, Endpoint.USERS.getPattern()).hasRole(UserRole.ADMIN.name())
                .requestMatchers(HttpMethod.GET, Endpoint.USERS_DETAIL.getPattern()).hasAnyRole(UserRole.ADMIN.name(),UserRole.USER.name())
                .requestMatchers(Endpoint.SOCIETIES.getPattern()).hasAnyRole(SecurityExpressions.UserRole.ADMIN.name(),UserRole.USER.name())
                .requestMatchers(Endpoint.SOCIETIES_DETAIL.getPattern()).authenticated()
            );

        // Public endpoints
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(HttpMethod.POST, Endpoint.USERS.getPattern()).permitAll()
                .requestMatchers(Endpoint.AUTH.getPattern()).permitAll()
                .anyRequest().permitAll()
            );

        // Disable CSRF
        http.csrf().disable();
        // Enable CORS filter
        http.cors();

        // Disable form login authentication
        http.formLogin().disable();
        // Disable http basic authentication
        http.httpBasic().disable();

        // Exception handling
        http.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandlerImpl() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
            }
        });

        // Avoid creating session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // JWT filter
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
