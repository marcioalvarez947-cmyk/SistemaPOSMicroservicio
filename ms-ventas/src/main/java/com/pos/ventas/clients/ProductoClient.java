package com.pos.ventas.clients;

import com.pos.ventas.dtos.remote.ProductoRemoto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-producto", url = "${ms.producto.url}")
public interface ProductoClient {

    @GetMapping("/api/v1/productos/{id}")
    ProductoRemoto obtenerPorId(@PathVariable("id") Long id);

    @GetMapping("/api/v1/productos/codigo/{codigo}")
    ProductoRemoto obtenerPorCodigoBarras(@PathVariable("codigo") String codigo);
}
