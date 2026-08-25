package com.smartbank.apigateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class GatewayExceptionHandler implements WebExceptionHandler {

    private final ObjectMapper objectMapper;

    public GatewayExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(
            ServerWebExchange exchange,
            Throwable ex
    ) {

        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = "Internal Server Error";
        String message = "An unexpected error occurred";

        GatewayErrorResponse response = new GatewayErrorResponse(
                Instant.now(),
                status.value(),
                error,
                message,
                exchange.getRequest().getPath().value()
        );

        byte[] bytes;

        try {
            bytes = objectMapper.writeValueAsString(response)
                    .getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            return Mono.error(e);
        }

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        DataBuffer buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(bytes);

        return exchange.getResponse().writeWith(
                Mono.just(buffer)
        );
    }
}