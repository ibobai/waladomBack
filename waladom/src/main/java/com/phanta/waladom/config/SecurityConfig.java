package com.phanta.waladom.config;

//import com.phanta.waladom.oauthToken.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import com.phanta.waladom.oauthToken.JwtAuthenticationFilter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity // Enables role-based method security using annotations like @Secured or @PreAuthorize
//public class SecurityConfig {
//
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customEntryPoint) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // Disable CSRF as we're using JWT (stateless authentication)
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/api/auth/login", "/api/auth/refresh", "/api/user/createDTO","/api/report/**"
//                               ,"/api/verification/**", "/public/**","/api/user/password/**", "/api/user/register/create","/api/user/email/validate",
//                                "/api/upload/files","/api/user/phone/validate").permitAll() // Public endpoints
//                        .requestMatchers("/api/auth/secure").hasRole("ADMIN") // Restricted to admins
//                        .anyRequest().authenticated() // All other requests require authentication
//                )
//
//                .exceptionHandling(exception -> exception
//                        .authenticationEntryPoint(customEntryPoint) // Use custom entry point
//                )
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless sessions
//                )
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter
//
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}

import com.phanta.waladom.config.CustomAuthenticationEntryPoint;
import com.phanta.waladom.oauthToken.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationEntryPoint customEntryPoint) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disable CSRF (for JWT-based authentication)
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/auth/login", "/api/auth/refresh", "/api/user/createDTO", "/api/report/**",
                                "/api/verification/**", "/public/**", "/api/user/password/**", "/api/user/register/create",
                                "/api/user/email/validate", "/api/upload/files", "/api/user/phone/validate"
                        ).permitAll() // Public endpoints
                        .requestMatchers("/api/auth/secure").hasRole("ADMIN") // Restricted to admins
                        .anyRequest().authenticated() // All other requests require authentication
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customEntryPoint) // Use custom entry point
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "https://zp1v56uxy8rdx5ypatb0ockcb9tr6a-oci3--5173--d20a0a75.local-credentialless.webcontainer-api.io",
                "http://localhost:5173",
                "http://localhost:3000",
                "https://waladom.org"
        )); // Add your frontend URLs here
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowCredentials(true); // If using cookies/auth headers

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
