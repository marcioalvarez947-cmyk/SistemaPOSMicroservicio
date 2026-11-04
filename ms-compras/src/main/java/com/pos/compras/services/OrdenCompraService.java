package com.pos.compras.services;

import com.pos.compras.dtos.OrdenCompraRequest;
import com.pos.compras.dtos.OrdenCompraResponse;
import com.pos.compras.dtos.OrdenCompraResumenResponse;
import com.pos.compras.dtos.PageResponse;
import com.pos.compras.enums.EstadoOrdenCompra;
import org.springframework.data.domain.Pageable;

public interface OrdenCompraService {

    OrdenCompraResponse crear(OrdenCompraRequest request);

    OrdenCompraResponse obtenerPorId(Long id);

    PageResponse<OrdenCompraResumenResponse> listar(EstadoOrdenCompra estado, Pageable pageable);

    /** Marca la orden como RECEPCIONADA y aumenta el stock en ms-inventario. */
    OrdenCompraResponse recepcionar(Long id);

    OrdenCompraResponse anular(Long id, String motivo);
}
