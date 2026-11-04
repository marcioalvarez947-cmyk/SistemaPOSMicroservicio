package com.pos.inventario.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Lote de movimientos que se aplica en UNA sola transacción: o entran/salen todas las líneas o ninguna.
 * referenciaTipo y motivo llegan como texto (VENTA, COMPRA, ANULACION_VENTA...) para no acoplar enums entre servicios.
 */
public record MovimientoLoteRequest(

        @NotBlank(message = "El tipo de referencia es obligatorio")
        String referenciaTipo,

        /** Id de la venta u orden de compra que origina el movimiento. */
        Long referenciaId,

        @NotBlank(message = "El motivo es obligatorio")
        String motivo,

        @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
        String observacion,

        @NotEmpty(message = "Debe indicar al menos una línea")
        @Valid
        List<LineaMovimientoRequest> lineas) {
}
