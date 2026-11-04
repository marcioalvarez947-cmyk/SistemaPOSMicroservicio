package com.pos.ventas.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pos.ventas.exceptions.BusinessRuleException;
import com.pos.ventas.exceptions.ResourceNotFoundException;
import com.pos.ventas.exceptions.ServiceUnavailableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

/**
 * Traduce los errores HTTP de los otros microservicios a las excepciones de este servicio,
 * conservando el mensaje original (campo "message" del ApiError remoto).
 */
@Configuration
public class FeignConfig {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            String detalle = extraerMensaje(response);
            return switch (response.status()) {
                case 404 -> new ResourceNotFoundException(detalle);
                case 400, 409, 422 -> new BusinessRuleException(detalle);
                default -> new ServiceUnavailableException(
                        "El servicio remoto respondió con error " + response.status() + ": " + detalle);
            };
        };
    }

    private String extraerMensaje(Response response) {
        if (response.body() == null) {
            return "sin detalle";
        }
        try (InputStream in = response.body().asInputStream()) {
            JsonNode node = OBJECT_MAPPER.readTree(in);
            return node.path("message").asText("sin detalle");
        } catch (IOException e) {
            return "sin detalle";
        }
    }
}
