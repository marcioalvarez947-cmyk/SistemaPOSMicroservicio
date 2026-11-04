package com.pos.ventas.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Una línea del carrito. Se identifica el producto por codigoBarras (lo que lee el escáner)
 * o por productoId (cuando se elige de una lista). Basta con uno de los dos.
 */
public record LineaVentaRequest(

        Long productoId,

        @Size(max = 50, message = "El código de barras no puede superar los 50 caracteres")
        String codigoBarras,

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        Integer cantidad) {
}
