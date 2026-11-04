package com.pos.compras.dtos;

import com.pos.compras.enums.EstadoOrdenCompra;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenCompraResponse(
        Long id,
        String numeroOrden,
        Long proveedorId,
        String proveedorRazonSocial,
        LocalDate fechaEmision,
        LocalDate fechaEntregaEsperada,
        LocalDateTime fechaRecepcion,
        EstadoOrdenCompra estado,
        BigDecimal subtotal,
        BigDecimal total,
        String observaciones,
        String motivoAnulacion,
        List<DetalleOrdenCompraResponse> detalles) {
}
