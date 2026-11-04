package com.pos.compras.services;

import com.pos.compras.dtos.PageResponse;
import com.pos.compras.dtos.ProveedorRequest;
import com.pos.compras.dtos.ProveedorResponse;
import org.springframework.data.domain.Pageable;

public interface ProveedorService {

    PageResponse<ProveedorResponse> listar(Pageable pageable);

    ProveedorResponse obtenerPorId(Long id);

    ProveedorResponse crear(ProveedorRequest request);

    ProveedorResponse actualizar(Long id, ProveedorRequest request);

    void desactivar(Long id);
}
