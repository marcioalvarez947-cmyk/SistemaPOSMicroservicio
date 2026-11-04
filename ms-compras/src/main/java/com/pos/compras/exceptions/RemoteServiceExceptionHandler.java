package com.pos.compras.exceptions;

import feign.RetryableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Feign lanza RetryableException cuando el otro servicio está caído o tarda demasiado
 * (connection refused / timeout). Se responde 503 en lugar de un 500 genérico.
 */
@RestControllerAdvice
public class RemoteServiceExceptionHandler {

    @ExceptionHandler(RetryableException.class)
    public ResponseEntity<ApiError> handleRemoteDown(RetryableException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.SERVICE_UNAVAILABLE;
        ApiError body = new ApiError(LocalDateTime.now(), status.value(), status.getReasonPhrase(),
                "Un servicio requerido no está disponible. Intenta nuevamente en unos segundos.",
                req.getRequestURI(), null);
        return ResponseEntity.status(status).body(body);
    }
}
