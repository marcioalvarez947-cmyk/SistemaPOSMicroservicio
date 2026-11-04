package com.pos.ventas.clients;

import com.pos.ventas.dtos.remote.MovimientoLoteRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventario", url = "${ms.inventario.url}")
public interface InventarioClient {

    /** Descuenta stock por una venta. Falla con 422 si algún producto no alcanza. */
    @PostMapping("/api/v1/inventario/salidas")
    void registrarSalidas(@RequestBody MovimientoLoteRequest request);

    /** Devuelve stock al anular una venta (compensación). */
    @PostMapping("/api/v1/inventario/entradas")
    void registrarEntradas(@RequestBody MovimientoLoteRequest request);
}
