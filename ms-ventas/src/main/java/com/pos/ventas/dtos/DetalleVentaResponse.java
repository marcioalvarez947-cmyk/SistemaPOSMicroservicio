package com.pos.ventas.dtos;

import java.math.BigDecimal;

public record DetalleVentaResponse(
        Long id,
        Long productoId,
        String codigoBarras,
        String nombreProducto,
        Integer cantidad,
        BigDecimal precioUnitario,
        BigDecimal descuento,
        BigDecimal subtotal) {
}
