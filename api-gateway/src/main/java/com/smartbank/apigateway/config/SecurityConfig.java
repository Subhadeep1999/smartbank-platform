package com.smartbank.apigateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartbank.apigateway.exception.GatewaySecurityErrorHandler;
import com.smartbank.apigateway.security.JwtAuthenticationConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ObjectMapper objectMapper
    ) {

        GatewaySecurityErrorHandler errorHandler =
                new GatewaySecurityErrorHandler(objectMapper);

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .authorizeExchange(exchange -> exchange

                        // =========================
                        // PUBLIC APIs
                        // =========================

                        .pathMatchers(
                                "/api/v1/auth/**",
                                "/actuator/health"
                        )
                        .permitAll()


                        // =========================
                        // ACCOUNT APIs
                        // =========================

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/accounts"
                        )
                        .hasAuthority("ACCOUNT_CREATE")

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/accounts/**"
                        )
                        .hasAuthority("ACCOUNT_READ")

                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/accounts/**"
                        )
                        .hasAuthority("ACCOUNT_UPDATE")


                        // =========================
                        // CUSTOMER APIs
                        // =========================

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/customers"
                        )
                        .hasAuthority("CUSTOMER_CREATE")

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/customers/**"
                        )
                        .hasAuthority("CUSTOMER_READ")

                        .pathMatchers(
                                HttpMethod.PUT,
                                "/api/v1/customers/**"
                        )
                        .hasAuthority("CUSTOMER_UPDATE")

                        .pathMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/customers/*/deactivate"
                        )
                        .hasAuthority("CUSTOMER_DEACTIVATE")

                        // =========================
                        // TRANSACTION APIs
                        // =========================

                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/transactions"
                        )
                        .hasAuthority("TRANSACTION_CREATE")

                        .pathMatchers(
                                HttpMethod.GET,
                                "/api/v1/transactions/**"
                        )
                        .hasAuthority("TRANSACTION_READ")

                        // =========================
                        // INTERNAL OUTBOX APIs
                        // =========================
                        
                        .pathMatchers(
                                HttpMethod.POST,
                                "/api/v1/internal/outbox/**"
                        )
                        .hasAuthority("OUTBOX_REPLAY")


                        // =========================
                        // EVERYTHING ELSE
                        // =========================


                        .anyExchange()
                        .authenticated()
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler)
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(errorHandler)
                        .accessDeniedHandler(errorHandler)

                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        new ReactiveJwtAuthenticationConverterAdapter(
                                                new JwtAuthenticationConverter()
                                        )
                                )
                        )
                )

                .build();
    }
}