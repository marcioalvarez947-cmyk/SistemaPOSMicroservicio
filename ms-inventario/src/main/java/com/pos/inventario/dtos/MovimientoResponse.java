package com.pos.inventario.dtos;

import com.pos.inventario.enums.MotivoMovimiento;
import com.pos.inventario.enums.TipoMovimiento;
import com.pos.inventario.enums.TipoReferencia;

import java.time.LocalDateTime;

public record MovimientoResponse(
        Long id,
        Long productoId,
        TipoMovimiento tipo,
        MotivoMovimiento motivo,
        Integer cantidad,
        Integer stockAnterior,
        Integer stockPosterior,
        TipoReferencia referenciaTipo,
        Long referenciaId,
        String observacion,
        LocalDateTime fechaMovimiento) {
}
