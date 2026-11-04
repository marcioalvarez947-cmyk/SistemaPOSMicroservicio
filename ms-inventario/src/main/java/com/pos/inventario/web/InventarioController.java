package com.pos.inventario.web;

import com.pos.inventario.dtos.AjusteRequest;
import com.pos.inventario.dtos.MovimientoLoteRequest;
import com.pos.inventario.dtos.MovimientoResponse;
import com.pos.inventario.dtos.PageResponse;
import com.pos.inventario.dtos.ParametrosStockRequest;
import com.pos.inventario.dtos.StockResponse;
import com.pos.inventario.services.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventario")
@RequiredArgsConstructor
public class InventarioController {

    private final InventarioService service;

    @GetMapping
    public PageResponse<StockResponse> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listar(PageRequest.of(page, size, Sort.by("productoId")));
    }

    @GetMapping("/bajo-stock")
    public List<StockResponse> stockBajo() {
        return service.listarStockBajo();
    }

    @GetMapping("/{productoId}")
    public StockResponse obtenerStock(@PathVariable Long productoId) {
        return service.obtenerStock(productoId);
    }

    @GetMapping("/{productoId}/movimientos")
    public PageResponse<MovimientoResponse> movimientos(
            @PathVariable Long productoId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return service.listarMovimientos(productoId, PageRequest.of(page, size));
    }

    /** Lo llaman ms-compras (recepción) y ms-ventas (anulación). */
    @PostMapping("/entradas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void entradas(@Valid @RequestBody MovimientoLoteRequest request) {
        service.registrarEntradas(request);
    }

    /** Lo llama ms-ventas al cobrar. */
    @PostMapping("/salidas")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void salidas(@Valid @RequestBody MovimientoLoteRequest request) {
        service.registrarSalidas(request);
    }

    @PostMapping("/ajustes")
    public StockResponse ajustar(@Valid @RequestBody AjusteRequest request) {
        return service.ajustar(request);
    }

    @PutMapping("/{productoId}/parametros")
    public StockResponse parametros(@PathVariable Long productoId,
                                    @Valid @RequestBody ParametrosStockRequest request) {
        return service.actualizarParametros(productoId, request);
    }
}
