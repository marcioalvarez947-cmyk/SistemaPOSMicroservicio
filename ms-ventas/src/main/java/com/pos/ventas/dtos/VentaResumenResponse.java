package com.pos.ventas.dtos;

import com.pos.ventas.enums.EstadoVenta;
import com.pos.ventas.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Versión liviana para listados (sin líneas). */
public record VentaResumenResponse(
        Long id,
        String numeroTicket,
        LocalDateTime fechaVenta,
        Long clienteId,
        MetodoPago metodoPago,
        BigDecimal total,
        EstadoVenta estado) {
}
