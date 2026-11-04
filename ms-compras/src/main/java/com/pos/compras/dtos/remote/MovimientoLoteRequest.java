package com.pos.compras.dtos.remote;

import java.util.List;

/**
 * Lote de movimientos que ms-inventario aplica en UNA sola transacción (todo o nada).
 * referenciaTipo / motivo viajan como texto para no acoplar los enums entre servicios.
 */
public record MovimientoLoteRequest(
        String referenciaTipo,
        Long referenciaId,
        String motivo,
        String observacion,
        List<LineaMovimientoRequest> lineas) {
}
