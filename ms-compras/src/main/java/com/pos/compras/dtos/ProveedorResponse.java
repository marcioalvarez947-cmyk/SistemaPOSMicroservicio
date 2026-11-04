package com.pos.compras.dtos;

import java.time.LocalDateTime;

public record ProveedorResponse(
        Long id,
        String ruc,
        String razonSocial,
        String nombreComercial,
        String personaContacto,
        String telefono,
        String email,
        String direccion,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
