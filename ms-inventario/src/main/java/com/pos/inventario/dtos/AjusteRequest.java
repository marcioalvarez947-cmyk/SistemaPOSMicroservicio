package com.pos.inventario.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Ajuste por conteo físico: se indica la cantidad REAL que hay en almacén. */
public record AjusteRequest(

        @NotNull(message = "El productoId es obligatorio")
        Long productoId,

        @NotNull(message = "La cantidad real es obligatoria")
        @Min(value = 0, message = "La cantidad real no puede ser negativa")
        Integer cantidadReal,

        @NotBlank(message = "La observación es obligatoria")
        @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
        String observacion) {
}
