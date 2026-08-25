package com.smartbank.apigateway.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class GatewaySecurityErrorHandler
        implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public GatewaySecurityErrorHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(
            ServerWebExchange exchange,
            AuthenticationException exception
    ) {

        return writeError(
                exchange,
                HttpStatus.UNAUTHORIZED,
                "Unauthorized",
                "Authentication is required to access this resource"
        );
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            org.springframework.security.access.AccessDeniedException exception
    ) {

        return writeError(
                exchange,
                HttpStatus.FORBIDDEN,
                "Forbidden",
                "You do not have permission to access this resource"
        );
    }

    private Mono<Void> writeError(
            ServerWebExchange exchange,
            HttpStatus status,
            String error,
            String message
    ) {

        GatewayErrorResponse response = new GatewayErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                exchange.getRequest().getPath().value()
        );

        try {

            byte[] bytes = objectMapper
                    .writeValueAsString(response)
                    .getBytes(StandardCharsets.UTF_8);

            exchange.getResponse().setStatusCode(status);

            exchange.getResponse()
                    .getHeaders()
                    .setContentType(MediaType.APPLICATION_JSON);

            return exchange.getResponse()
                    .writeWith(
                            Mono.just(
                                    exchange.getResponse()
                                            .bufferFactory()
                                            .wrap(bytes)
                            )
                    );

        } catch (Exception ex) {
            return Mono.error(ex);
        }
    }
}