package com.pos.inventario.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LineaMovimientoRequest(

        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad) {
}
