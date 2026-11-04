package com.pos.compras.clients;

import com.pos.compras.dtos.remote.MovimientoLoteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario", url = "${ms.inventario.url}")
public interface InventarioClient {

    /**
     * Registra la entrada de stock al recepcionar una orden.
     * Es idempotente: reenviar la misma orden no duplica el stock.
     */
    @PostMapping("/api/v1/inventario/entradas")
    void registrarEntradas(@RequestBody MovimientoLoteRequest request);
}
