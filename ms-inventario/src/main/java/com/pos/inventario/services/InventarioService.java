package com.pos.inventario.services;

import com.pos.inventario.dtos.AjusteRequest;
import com.pos.inventario.dtos.MovimientoLoteRequest;
import com.pos.inventario.dtos.MovimientoResponse;
import com.pos.inventario.dtos.PageResponse;
import com.pos.inventario.dtos.ParametrosStockRequest;
import com.pos.inventario.dtos.StockResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventarioService {

    PageResponse<StockResponse> listar(Pageable pageable);

    StockResponse obtenerStock(Long productoId);

    List<StockResponse> listarStockBajo();

    PageResponse<MovimientoResponse> listarMovimientos(Long productoId, Pageable pageable);

    /** Aumenta stock (compra recepcionada, anulación de venta). Idempotente por documento. */
    void registrarEntradas(MovimientoLoteRequest request);

    /** Descuenta stock (venta). Todo o nada; falla con 422 si algún producto no alcanza. */
    void registrarSalidas(MovimientoLoteRequest request);

    StockResponse ajustar(AjusteRequest request);

    StockResponse actualizarParametros(Long productoId, ParametrosStockRequest request);
}
