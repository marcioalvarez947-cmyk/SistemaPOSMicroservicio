package com.pos.compras.dtos;

import java.math.BigDecimal;

public record DetalleOrdenCompraResponse(
        Long id,
        Long productoId,
        String codigoBarras,
        String nombreProducto,
        Integer cantidad,
        BigDecimal costoUnitario,
        BigDecimal subtotal) {
}
