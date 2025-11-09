package com.explik.diybirdyapp.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling ->
                                exceptionHandling
//                                .accessDeniedHandler(new CustomAccessDeniedHandler())
                                        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                )
                .authorizeHttpRequests((auth) -> auth
                                // TODO Replace POST /reset-graph with a more secure approach in production
                                .requestMatchers(HttpMethod.POST, "reset-graph").permitAll()
                                .requestMatchers("/ping").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("v3/api-docs").permitAll()
                                .requestMatchers("/actuator/health").permitAll()
                                .anyRequest().authenticated()
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .invalidateHttpSession(true) // Invalidate the session
                        .clearAuthentication(true) // Clear authentication context
                        .deleteCookies("JSESSIONID") // Delete JSESSIONID cookie
                        .permitAll() // Allow access to the logout endpoint without authentication
                )
                // Enable sessions;
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        System.out.println("in authenticationManager");
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);

        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}