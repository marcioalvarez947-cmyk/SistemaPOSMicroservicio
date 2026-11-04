package com.pos.compras.dtos;

import com.pos.compras.enums.EstadoOrdenCompra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Versión liviana para listados (sin líneas). */
public record OrdenCompraResumenResponse(
        Long id,
        String numeroOrden,
        String proveedorRazonSocial,
        LocalDate fechaEmision,
        LocalDateTime fechaRecepcion,
        EstadoOrdenCompra estado,
        BigDecimal total) {
}
