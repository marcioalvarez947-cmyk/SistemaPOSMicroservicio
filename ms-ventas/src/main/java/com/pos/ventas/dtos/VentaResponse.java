package com.pos.ventas.dtos;

import com.pos.ventas.enums.EstadoVenta;
import com.pos.ventas.enums.MetodoPago;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Long id,
        String numeroTicket,
        LocalDateTime fechaVenta,
        Long clienteId,
        MetodoPago metodoPago,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal total,
        BigDecimal montoRecibido,
        BigDecimal vuelto,
        EstadoVenta estado,
        String observacion,
        String motivoAnulacion,
        LocalDateTime fechaAnulacion,
        List<DetalleVentaResponse> detalles) {
}
