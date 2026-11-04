package com.pos.producto.dtos;

import com.pos.producto.enums.UnidadMedida;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductoResponse(
        Long id,
        String codigoBarras,
        String sku,
        String nombre,
        String descripcion,
        Long categoriaId,
        String categoriaNombre,
        String marca,
        UnidadMedida unidadMedida,
        BigDecimal precioVenta,
        BigDecimal costoReferencial,
        String imagenUrl,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
