package com.pos.inventario.services;

import com.pos.inventario.dtos.AjusteRequest;
import com.pos.inventario.dtos.LineaMovimientoRequest;
import com.pos.inventario.dtos.MovimientoLoteRequest;
import com.pos.inventario.dtos.MovimientoResponse;
import com.pos.inventario.dtos.PageResponse;
import com.pos.inventario.dtos.ParametrosStockRequest;
import com.pos.inventario.dtos.StockResponse;
import com.pos.inventario.entities.MovimientoInventario;
import com.pos.inventario.entities.Stock;
import com.pos.inventario.enums.MotivoMovimiento;
import com.pos.inventario.enums.TipoMovimiento;
import com.pos.inventario.enums.TipoReferencia;
import com.pos.inventario.exceptions.BusinessRuleException;
import com.pos.inventario.repositories.MovimientoInventarioRepository;
import com.pos.inventario.repositories.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventarioServiceImpl implements InventarioService {

    private final StockRepository stockRepository;
    private final MovimientoInventarioRepository movimientoRepository;

    // ---------------------------------------------------------------- consultas

    @Override
    public PageResponse<StockResponse> listar(Pageable pageable) {
        return PageResponse.from(stockRepository.findAll(pageable).map(this::toResponse));
    }

    @Override
    public StockResponse obtenerStock(Long productoId) {
        // Un producto sin fila de stock simplemente tiene 0 unidades.
        return stockRepository.findByProductoId(productoId)
                .map(this::toResponse)
                .orElseGet(() -> new StockResponse(productoId, 0, 0, null, null, false, null));
    }

    @Override
    public List<StockResponse> listarStockBajo() {
        return stockRepository.findConStockBajo().stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<MovimientoResponse> listarMovimientos(Long productoId, Pageable pageable) {
        return PageResponse.from(movimientoRepository
                .findByProductoIdOrderByFechaMovimientoDesc(productoId, pageable)
                .map(this::toResponse));
    }

    // ---------------------------------------------------------------- operaciones

    @Override
    @Transactional
    public void registrarEntradas(MovimientoLoteRequest request) {
        MotivoMovimiento motivo = parsearMotivo(request.motivo());
        TipoReferencia referencia = parsearReferencia(request.referenciaTipo());

        for (LineaMovimientoRequest linea : ordenarPorProducto(request.lineas())) {
            Long productoId = linea.productoId();
            if (yaRegistrado(productoId, referencia, request.referenciaId(), motivo)) {
                log.info("Entrada ya registrada (idempotencia): producto={} {}#{}",
                        productoId, referencia, request.referenciaId());
                continue;
            }
            obtenerOCrearStock(productoId);
            stockRepository.incrementar(productoId, linea.cantidad());

            int posterior = cantidadActual(productoId);
            guardarMovimiento(productoId, TipoMovimiento.ENTRADA, motivo, linea.cantidad(),
                    posterior - linea.cantidad(), posterior, referencia, request.referenciaId(),
                    request.observacion());
        }
    }

    @Override
    @Transactional
    public void registrarSalidas(MovimientoLoteRequest request) {
        MotivoMovimiento motivo = parsearMotivo(request.motivo());
        TipoReferencia referencia = parsearReferencia(request.referenciaTipo());

        // Orden fijo por producto: evita bloqueos cruzados si dos cajas venden a la vez.
        for (LineaMovimientoRequest linea : ordenarPorProducto(request.lineas())) {
            Long productoId = linea.productoId();
            if (yaRegistrado(productoId, referencia, request.referenciaId(), motivo)) {
                log.info("Salida ya registrada (idempotencia): producto={} {}#{}",
                        productoId, referencia, request.referenciaId());
                continue;
            }
            int filas = stockRepository.descontar(productoId, linea.cantidad());
            if (filas == 0) {
                throw new BusinessRuleException(
                        "Stock insuficiente para el producto %d (disponible: %d, solicitado: %d)"
                                .formatted(productoId, cantidadActual(productoId), linea.cantidad()));
            }
            int posterior = cantidadActual(productoId);
            guardarMovimiento(productoId, TipoMovimiento.SALIDA, motivo, linea.cantidad(),
                    posterior + linea.cantidad(), posterior, referencia, request.referenciaId(),
                    request.observacion());
        }
        // Si alguna línea lanzó la excepción, la transacción completa se revierte (todo o nada).
    }

    @Override
    @Transactional
    public StockResponse ajustar(AjusteRequest request) {
        Stock stock = obtenerOCrearStock(request.productoId());
        int anterior = stock.getCantidad();
        int diferencia = request.cantidadReal() - anterior;
        if (diferencia == 0) {
            return toResponse(stock);
        }
        stock.setCantidad(request.cantidadReal());
        stockRepository.save(stock);

        guardarMovimiento(request.productoId(),
                diferencia > 0 ? TipoMovimiento.ENTRADA : TipoMovimiento.SALIDA,
                MotivoMovimiento.AJUSTE, Math.abs(diferencia), anterior, request.cantidadReal(),
                TipoReferencia.AJUSTE, null, request.observacion());
        log.info("Ajuste de inventario producto={} {} -> {}", request.productoId(), anterior, request.cantidadReal());
        return toResponse(stock);
    }

    @Override
    @Transactional
    public StockResponse actualizarParametros(Long productoId, ParametrosStockRequest request) {
        Stock stock = obtenerOCrearStock(productoId);
        if (request.stockMinimo() != null) {
            stock.setStockMinimo(request.stockMinimo());
        }
        if (request.stockMaximo() != null) {
            stock.setStockMaximo(request.stockMaximo());
        }
        if (request.ubicacion() != null) {
            stock.setUbicacion(request.ubicacion());
        }
        return toResponse(stockRepository.save(stock));
    }

    // ---------------------------------------------------------------- helpers

    private Stock obtenerOCrearStock(Long productoId) {
        return stockRepository.findByProductoId(productoId)
                .orElseGet(() -> stockRepository.save(Stock.builder().productoId(productoId).build()));
    }

    private int cantidadActual(Long productoId) {
        return stockRepository.findByProductoId(productoId).map(Stock::getCantidad).orElse(0);
    }

    /** Idempotencia: si el documento ya generó este movimiento, no se repite. */
    private boolean yaRegistrado(Long productoId, TipoReferencia referencia, Long referenciaId,
                                 MotivoMovimiento motivo) {
        return referenciaId != null && movimientoRepository
                .existsByProductoIdAndReferenciaTipoAndReferenciaIdAndMotivo(
                        productoId, referencia, referenciaId, motivo);
    }

    private List<LineaMovimientoRequest> ordenarPorProducto(List<LineaMovimientoRequest> lineas) {
        return lineas.stream().sorted(Comparator.comparing(LineaMovimientoRequest::productoId)).toList();
    }

    private void guardarMovimiento(Long productoId, TipoMovimiento tipo, MotivoMovimiento motivo,
                                   int cantidad, int anterior, int posterior,
                                   TipoReferencia referencia, Long referenciaId, String observacion) {
        movimientoRepository.save(MovimientoInventario.builder()
                .productoId(productoId)
                .tipo(tipo)
                .motivo(motivo)
                .cantidad(cantidad)
                .stockAnterior(anterior)
                .stockPosterior(posterior)
                .referenciaTipo(referencia)
                .referenciaId(referenciaId)
                .observacion(observacion)
                .fechaMovimiento(LocalDateTime.now())
                .build());
    }

    private MotivoMovimiento parsearMotivo(String valor) {
        try {
            return MotivoMovimiento.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Motivo de movimiento inválido: " + valor);
        }
    }

    private TipoReferencia parsearReferencia(String valor) {
        try {
            return TipoReferencia.valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessRuleException("Tipo de referencia inválido: " + valor);
        }
    }

    private StockResponse toResponse(Stock s) {
        return new StockResponse(s.getProductoId(), s.getCantidad(), s.getStockMinimo(), s.getStockMaximo(),
                s.getUbicacion(), s.getCantidad() <= s.getStockMinimo(), s.getFechaActualizacion());
    }

    private MovimientoResponse toResponse(MovimientoInventario m) {
        return new MovimientoResponse(m.getId(), m.getProductoId(), m.getTipo(), m.getMotivo(), m.getCantidad(),
                m.getStockAnterior(), m.getStockPosterior(), m.getReferenciaTipo(), m.getReferenciaId(),
                m.getObservacion(), m.getFechaMovimiento());
    }
}
