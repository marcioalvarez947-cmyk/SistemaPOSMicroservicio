package com.pos.cliente.dtos;

import com.pos.cliente.enums.TipoDocumento;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClienteResponse(
        Long id,
        TipoDocumento tipoDocumento,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String razonSocial,
        String email,
        String telefono,
        String direccion,
        LocalDate fechaNacimiento,
        Boolean activo,
        LocalDateTime fechaCreacion,
        LocalDateTime fechaActualizacion) {
}
