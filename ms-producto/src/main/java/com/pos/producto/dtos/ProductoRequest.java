package com.pos.producto.dtos;

import com.pos.producto.enums.UnidadMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductoRequest(

        @NotBlank(message = "El código de barras es obligatorio")
        @Size(max = 50, message = "El código de barras no puede superar los 50 caracteres")
        String codigoBarras,

        @Size(max = 40, message = "El SKU no puede superar los 40 caracteres")
        String sku,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        @NotNull(message = "La categoría es obligatoria")
        Long categoriaId,

        @Size(max = 80, message = "La marca no puede superar los 80 caracteres")
        String marca,

        /** Si no se envía, se asume UNIDAD. */
        UnidadMedida unidadMedida,

        @NotNull(message = "El precio de venta es obligatorio")
        @DecimalMin(value = "0.01", message = "El precio de venta debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "El precio admite hasta 2 decimales")
        BigDecimal precioVenta,

        @DecimalMin(value = "0.00", message = "El costo referencial no puede ser negativo")
        @Digits(integer = 10, fraction = 2, message = "El costo admite hasta 2 decimales")
        BigDecimal costoReferencial,

        @Size(max = 300, message = "La URL de la imagen no puede superar los 300 caracteres")
        String imagenUrl) {
}
