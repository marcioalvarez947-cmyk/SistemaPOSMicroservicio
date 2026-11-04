package com.pos.compras.web;

import com.pos.compras.dtos.AnularOrdenRequest;
import com.pos.compras.dtos.OrdenCompraRequest;
import com.pos.compras.dtos.OrdenCompraResponse;
import com.pos.compras.dtos.OrdenCompraResumenResponse;
import com.pos.compras.dtos.PageResponse;
import com.pos.compras.enums.EstadoOrdenCompra;
import com.pos.compras.services.OrdenCompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/ordenes-compra")
@RequiredArgsConstructor
public class OrdenCompraController {

    private final OrdenCompraService service;

    @PostMapping
    public ResponseEntity<OrdenCompraResponse> crear(@Valid @RequestBody OrdenCompraRequest request) {
        OrdenCompraResponse creada = service.crear(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(creada.id()).toUri();
        return ResponseEntity.created(location).body(creada);
    }

    @GetMapping
    public PageResponse<OrdenCompraResumenResponse> listar(
            @RequestParam(required = false) EstadoOrdenCompra estado,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return service.listar(estado, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    @GetMapping("/{id}")
    public OrdenCompraResponse obtener(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    /** Cambia el estado a RECEPCIONADA y el stock aumenta automáticamente. */
    @PostMapping("/{id}/recepcionar")
    public OrdenCompraResponse recepcionar(@PathVariable Long id) {
        return service.recepcionar(id);
    }

    @PostMapping("/{id}/anular")
    public OrdenCompraResponse anular(@PathVariable Long id, @Valid @RequestBody AnularOrdenRequest request) {
        return service.anular(id, request.motivo());
    }
}
