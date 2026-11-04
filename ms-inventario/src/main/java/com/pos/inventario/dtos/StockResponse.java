package com.pos.inventario.dtos;

import java.time.LocalDateTime;

public record StockResponse(
        Long productoId,
        Integer cantidad,
        Integer stockMinimo,
        Integer stockMaximo,
        String ubicacion,
        boolean stockBajo,
        LocalDateTime fechaActualizacion) {
}
