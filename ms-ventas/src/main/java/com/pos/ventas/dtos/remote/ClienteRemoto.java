package com.pos.ventas.dtos.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ClienteRemoto(
        Long id,
        String nombres,
        String apellidos,
        Boolean activo) {
}
