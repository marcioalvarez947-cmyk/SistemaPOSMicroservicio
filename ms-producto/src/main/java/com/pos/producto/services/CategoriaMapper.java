package com.pos.producto.services;

import com.pos.producto.dtos.CategoriaRequest;
import com.pos.producto.dtos.CategoriaResponse;
import com.pos.producto.entities.Categoria;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {

    public Categoria toEntity(CategoriaRequest r) {
        return Categoria.builder()
                .nombre(r.nombre().trim())
                .descripcion(r.descripcion())
                .build();
    }

    public void updateEntity(Categoria c, CategoriaRequest r) {
        c.setNombre(r.nombre().trim());
        c.setDescripcion(r.descripcion());
    }

    public CategoriaResponse toResponse(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNombre(), c.getDescripcion(), c.getActivo(),
                c.getFechaCreacion(), c.getFechaActualizacion());
    }
}
