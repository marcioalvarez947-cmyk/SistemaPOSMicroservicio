package com.pos.inventario.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record ParametrosStockRequest(

        @Min(value = 0, message = "El stock mínimo no puede ser negativo")
        Integer stockMinimo,

        @Min(value = 0, message = "El stock máximo no puede ser negativo")
        Integer stockMaximo,

        @Size(max = 60, message = "La ubicación no puede superar los 60 caracteres")
        String ubicacion) {
}
