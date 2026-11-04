package com.pos.compras.services;

import com.pos.compras.dtos.ProveedorRequest;
import com.pos.compras.dtos.ProveedorResponse;
import com.pos.compras.entities.Proveedor;
import org.springframework.stereotype.Component;

@Component
public class ProveedorMapper {

    public Proveedor toEntity(ProveedorRequest r) {
        return Proveedor.builder()
                .ruc(r.ruc().trim())
                .razonSocial(r.razonSocial().trim())
                .nombreComercial(r.nombreComercial())
                .personaContacto(r.personaContacto())
                .telefono(r.telefono())
                .email(r.email())
                .direccion(r.direccion())
                .build();
    }

    public void updateEntity(Proveedor p, ProveedorRequest r) {
        p.setRuc(r.ruc().trim());
        p.setRazonSocial(r.razonSocial().trim());
        p.setNombreComercial(r.nombreComercial());
        p.setPersonaContacto(r.personaContacto());
        p.setTelefono(r.telefono());
        p.setEmail(r.email());
        p.setDireccion(r.direccion());
    }

    public ProveedorResponse toResponse(Proveedor p) {
        return new ProveedorResponse(p.getId(), p.getRuc(), p.getRazonSocial(), p.getNombreComercial(),
                p.getPersonaContacto(), p.getTelefono(), p.getEmail(), p.getDireccion(), p.getActivo(),
                p.getFechaCreacion(), p.getFechaActualizacion());
    }
}
