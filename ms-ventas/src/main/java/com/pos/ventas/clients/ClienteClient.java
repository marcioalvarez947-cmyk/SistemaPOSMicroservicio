package com.pos.ventas.clients;

import com.pos.ventas.dtos.remote.ClienteRemoto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente", url = "${ms.cliente.url}")
public interface ClienteClient {

    @GetMapping("/api/v1/clientes/{id}")
    ClienteRemoto obtenerPorId(@PathVariable("id") Long id);
}
