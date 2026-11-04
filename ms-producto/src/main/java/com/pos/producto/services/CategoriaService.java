package com.pos.producto.services;

import com.pos.producto.dtos.CategoriaRequest;
import com.pos.producto.dtos.CategoriaResponse;

import java.util.List;

public interface CategoriaService {

    List<CategoriaResponse> listar();

    CategoriaResponse obtenerPorId(Long id);

    CategoriaResponse crear(CategoriaRequest request);

    CategoriaResponse actualizar(Long id, CategoriaRequest request);

    void desactivar(Long id);
}
