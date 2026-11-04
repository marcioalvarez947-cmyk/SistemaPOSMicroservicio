package com.pos.compras.dtos.remote;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductoRemoto(
        Long id,
        String codigoBarras,
        String nombre,
        BigDecimal precioVenta,
        Boolean activo) {
}
