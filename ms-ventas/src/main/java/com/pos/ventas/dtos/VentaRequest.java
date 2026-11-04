package com.pos.ventas.dtos;

import com.pos.ventas.enums.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record VentaRequest(

        /** Opcional. Si no se envía, es una venta de mostrador (cliente general). */
        Long clienteId,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        /** Obligatorio solo cuando el pago es en EFECTIVO. */
        @DecimalMin(value = "0.00", message = "El monto recibido no puede ser negativo")
        BigDecimal montoRecibido,

        @DecimalMin(value = "0.00", message = "El descuento no puede ser negativo")
        BigDecimal descuento,

        @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
        String observacion,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        @Valid
        List<LineaVentaRequest> lineas) {
}
