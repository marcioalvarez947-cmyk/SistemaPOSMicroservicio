package com.pos.cliente.services;

import com.pos.cliente.dtos.ClienteRequest;
import com.pos.cliente.dtos.ClienteResponse;
import com.pos.cliente.dtos.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    PageResponse<ClienteResponse> listar(String texto, Pageable pageable);

    ClienteResponse obtenerPorId(Long id);

    ClienteResponse crear(ClienteRequest request);

    ClienteResponse actualizar(Long id, ClienteRequest request);

    /** Baja lógica: el cliente queda inactivo pero conserva su historial de ventas. */
    void desactivar(Long id);
}
