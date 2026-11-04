package com.pos.compras.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProveedorRequest(

        @NotBlank(message = "El RUC es obligatorio")
        @Pattern(regexp = "^[0-9]{11}$", message = "El RUC debe tener 11 dígitos")
        String ruc,

        @NotBlank(message = "La razón social es obligatoria")
        @Size(max = 150, message = "La razón social no puede superar los 150 caracteres")
        String razonSocial,

        @Size(max = 150, message = "El nombre comercial no puede superar los 150 caracteres")
        String nombreComercial,

        @Size(max = 100, message = "La persona de contacto no puede superar los 100 caracteres")
        String personaContacto,

        @Pattern(regexp = "^[0-9+() -]{6,20}$", message = "El teléfono no tiene un formato válido")
        String telefono,

        @Email(message = "El email no tiene un formato válido")
        @Size(max = 120, message = "El email no puede superar los 120 caracteres")
        String email,

        @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
        String direccion) {
}
