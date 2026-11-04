package com.pos.cliente.services;

import com.pos.cliente.dtos.ClienteRequest;
import com.pos.cliente.dtos.ClienteResponse;
import com.pos.cliente.entities.Cliente;
import org.springframework.stereotype.Component;

/**
 * Mapper manual: explícito, sin dependencias extra y fácil de explicar.
 * (Alternativa: MapStruct, o BeanUtils.copyProperties como en clase.)
 */
@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest r) {
        return Cliente.builder()
                .tipoDocumento(r.tipoDocumento())
                .numeroDocumento(r.numeroDocumento().trim())
                .nombres(r.nombres().trim())
                .apellidos(r.apellidos())
                .razonSocial(r.razonSocial())
                .email(r.email())
                .telefono(r.telefono())
                .direccion(r.direccion())
                .fechaNacimiento(r.fechaNacimiento())
                .build();
    }

    public void updateEntity(Cliente c, ClienteRequest r) {
        c.setTipoDocumento(r.tipoDocumento());
        c.setNumeroDocumento(r.numeroDocumento().trim());
        c.setNombres(r.nombres().trim());
        c.setApellidos(r.apellidos());
        c.setRazonSocial(r.razonSocial());
        c.setEmail(r.email());
        c.setTelefono(r.telefono());
        c.setDireccion(r.direccion());
        c.setFechaNacimiento(r.fechaNacimiento());
    }

    public ClienteResponse toResponse(Cliente c) {
        return new ClienteResponse(
                c.getId(),
                c.getTipoDocumento(),
                c.getNumeroDocumento(),
                c.getNombres(),
                c.getApellidos(),
                c.getRazonSocial(),
                c.getEmail(),
                c.getTelefono(),
                c.getDireccion(),
                c.getFechaNacimiento(),
                c.getActivo(),
                c.getFechaCreacion(),
                c.getFechaActualizacion());
    }
}
