package com.pos.compras.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record OrdenCompraRequest(

        @NotNull(message = "El proveedor es obligatorio")
        Long proveedorId,

        @FutureOrPresent(message = "La fecha de entrega esperada no puede estar en el pasado")
        LocalDate fechaEntregaEsperada,

        @Size(max = 255, message = "Las observaciones no pueden superar los 255 caracteres")
        String observaciones,

        @NotEmpty(message = "La orden debe tener al menos un producto")
        @Valid
        List<LineaOrdenCompraRequest> lineas) {
}
