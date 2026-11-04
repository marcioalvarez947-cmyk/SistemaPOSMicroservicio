package com.pos.ventas.services;

import com.pos.ventas.clients.ClienteClient;
import com.pos.ventas.clients.InventarioClient;
import com.pos.ventas.clients.ProductoClient;
import com.pos.ventas.dtos.LineaVentaRequest;
import com.pos.ventas.dtos.PageResponse;
import com.pos.ventas.dtos.VentaRequest;
import com.pos.ventas.dtos.VentaResponse;
import com.pos.ventas.dtos.VentaResumenResponse;
import com.pos.ventas.dtos.remote.ClienteRemoto;
import com.pos.ventas.dtos.remote.LineaMovimientoRequest;
import com.pos.ventas.dtos.remote.MovimientoLoteRequest;
import com.pos.ventas.dtos.remote.ProductoRemoto;
import com.pos.ventas.entities.DetalleVenta;
import com.pos.ventas.entities.Venta;
import com.pos.ventas.enums.EstadoVenta;
import com.pos.ventas.enums.MetodoPago;
import com.pos.ventas.exceptions.BusinessRuleException;
import com.pos.ventas.exceptions.ResourceNotFoundException;
import com.pos.ventas.repositories.VentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final VentaMapper mapper;
    private final ProductoClient productoClient;
    private final ClienteClient clienteClient;
    private final InventarioClient inventarioClient;

    // ------------------------------------------------------------------ crear venta

    @Override
    @Transactional
    public VentaResponse crear(VentaRequest request) {
        validarCliente(request.clienteId());

        // 1) Resolver productos y precios REALES desde ms-producto (nunca se confía en la pantalla)
        List<DetalleVenta> detalles = resolverLineas(request.lineas());

        // 2) Totales y pago
        BigDecimal subtotal = detalles.stream().map(DetalleVenta::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal descuento = request.descuento() == null
                ? BigDecimal.ZERO : request.descuento().setScale(2, RoundingMode.HALF_UP);
        if (descuento.compareTo(subtotal) > 0) {
            throw new BusinessRuleException("El descuento (%s) no puede superar el subtotal (%s)"
                    .formatted(descuento, subtotal));
        }
        BigDecimal total = subtotal.subtract(descuento);

        BigDecimal recibido;
        BigDecimal vuelto;
        if (request.metodoPago() == MetodoPago.EFECTIVO) {
            if (request.montoRecibido() == null || request.montoRecibido().compareTo(total) < 0) {
                throw new BusinessRuleException(
                        "El monto recibido es menor al total a pagar (%s)".formatted(total));
            }
            recibido = request.montoRecibido().setScale(2, RoundingMode.HALF_UP);
            vuelto = recibido.subtract(total);
        } else {
            recibido = total;
            vuelto = BigDecimal.ZERO;
        }

        // 3) Guardar la venta (todavía dentro de la transacción) para obtener su id
        Venta venta = Venta.builder()
                .numeroTicket("TMP-" + UUID.randomUUID().toString().substring(0, 12))
                .fechaVenta(LocalDateTime.now())
                .clienteId(request.clienteId())
                .metodoPago(request.metodoPago())
                .subtotal(subtotal)
                .descuento(descuento)
                .total(total)
                .montoRecibido(recibido)
                .vuelto(vuelto)
                .estado(EstadoVenta.COMPLETADA)
                .observacion(request.observacion())
                .build();
        detalles.forEach(venta::agregarDetalle);
        venta = ventaRepository.saveAndFlush(venta);
        venta.setNumeroTicket("T-%06d".formatted(venta.getId()));

        // 4) Descontar stock (todo o nada). Si no alcanza, lanza 422 y la venta se revierte sola.
        List<LineaMovimientoRequest> lineasStock = lineasDeStock(venta);
        inventarioClient.registrarSalidas(new MovimientoLoteRequest(
                "VENTA", venta.getId(), "VENTA", "Venta " + venta.getNumeroTicket(), lineasStock));

        // 5) Compensación: si el stock ya se descontó pero la transacción termina en rollback
        //    (falla al guardar/confirmar), se devuelve el stock.
        registrarCompensacion(venta.getId(), lineasStock);

        log.info("Venta creada id={} ticket={} total={}", venta.getId(), venta.getNumeroTicket(), total);
        return mapper.toResponse(venta);
    }

    // ------------------------------------------------------------------ consultas

    @Override
    public VentaResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarConDetalles(id));
    }

    @Override
    public PageResponse<VentaResumenResponse> listar(EstadoVenta estado, LocalDate desde, LocalDate hasta,
                                                     Pageable pageable) {
        Page<Venta> page;
        boolean hayRango = desde != null && hasta != null;
        LocalDateTime inicio = hayRango ? desde.atStartOfDay() : null;
        LocalDateTime fin = hayRango ? hasta.plusDays(1).atStartOfDay() : null;

        if (estado != null && hayRango) {
            page = ventaRepository.findByEstadoAndFechaVentaBetween(estado, inicio, fin, pageable);
        } else if (estado != null) {
            page = ventaRepository.findByEstado(estado, pageable);
        } else if (hayRango) {
            page = ventaRepository.findByFechaVentaBetween(inicio, fin, pageable);
        } else {
            page = ventaRepository.findAll(pageable);
        }
        return PageResponse.from(page.map(mapper::toResumen));
    }

    // ------------------------------------------------------------------ anular

    @Override
    @Transactional
    public VentaResponse anular(Long id, String motivo) {
        Venta venta = buscarConDetalles(id);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessRuleException("La venta " + venta.getNumeroTicket() + " ya está anulada");
        }

        // Se devuelve el stock primero. Es idempotente en ms-inventario: si algo falla después
        // y se reintenta la anulación, el stock no se devuelve dos veces.
        inventarioClient.registrarEntradas(new MovimientoLoteRequest(
                "VENTA", venta.getId(), "ANULACION_VENTA",
                "Anulación de la venta " + venta.getNumeroTicket(), lineasDeStock(venta)));

        venta.setEstado(EstadoVenta.ANULADA);
        venta.setMotivoAnulacion(motivo);
        venta.setFechaAnulacion(LocalDateTime.now());
        log.info("Venta anulada id={} ticket={}", venta.getId(), venta.getNumeroTicket());
        return mapper.toResponse(ventaRepository.save(venta));
    }

    // ------------------------------------------------------------------ helpers

    private void validarCliente(Long clienteId) {
        if (clienteId == null) {
            return; // venta de mostrador
        }
        ClienteRemoto cliente = clienteClient.obtenerPorId(clienteId);
        if (!Boolean.TRUE.equals(cliente.activo())) {
            throw new BusinessRuleException("El cliente " + clienteId + " está inactivo");
        }
    }

    /** Resuelve cada línea contra ms-producto y suma las líneas repetidas del mismo producto. */
    private List<DetalleVenta> resolverLineas(List<LineaVentaRequest> lineas) {
        Map<Long, DetalleVenta> porProducto = new LinkedHashMap<>();
        for (LineaVentaRequest linea : lineas) {
            ProductoRemoto producto = resolverProducto(linea);
            if (!Boolean.TRUE.equals(producto.activo())) {
                throw new BusinessRuleException("El producto '%s' está inactivo".formatted(producto.nombre()));
            }
            DetalleVenta existente = porProducto.get(producto.id());
            if (existente != null) {
                existente.setCantidad(existente.getCantidad() + linea.cantidad());
            } else {
                porProducto.put(producto.id(), DetalleVenta.builder()
                        .productoId(producto.id())
                        .codigoBarras(producto.codigoBarras())
                        .nombreProducto(producto.nombre())
                        .cantidad(linea.cantidad())
                        .precioUnitario(producto.precioVenta())
                        .build());
            }
        }
        porProducto.values().forEach(d -> d.setSubtotal(
                d.getPrecioUnitario().multiply(BigDecimal.valueOf(d.getCantidad()))
                        .subtract(d.getDescuento())
                        .setScale(2, RoundingMode.HALF_UP)));
        return List.copyOf(porProducto.values());
    }

    private ProductoRemoto resolverProducto(LineaVentaRequest linea) {
        if (linea.productoId() != null) {
            return productoClient.obtenerPorId(linea.productoId());
        }
        if (linea.codigoBarras() != null && !linea.codigoBarras().isBlank()) {
            return productoClient.obtenerPorCodigoBarras(linea.codigoBarras().trim());
        }
        throw new BusinessRuleException("Cada línea debe indicar productoId o codigoBarras");
    }

    private List<LineaMovimientoRequest> lineasDeStock(Venta venta) {
        return venta.getDetalles().stream()
                .map(d -> new LineaMovimientoRequest(d.getProductoId(), d.getCantidad()))
                .toList();
    }

    private Venta buscarConDetalles(Long id) {
        return ventaRepository.findWithDetallesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada con id " + id));
    }

    /**
     * Acción compensatoria (saga): si la transacción de la venta termina en rollback DESPUÉS de haber
     * descontado el stock, se devuelve el stock en ms-inventario.
     */
    private void registrarCompensacion(Long ventaId, List<LineaMovimientoRequest> lineas) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status != STATUS_ROLLED_BACK) {
                    return;
                }
                try {
                    inventarioClient.registrarEntradas(new MovimientoLoteRequest(
                            "VENTA", ventaId, "ANULACION_VENTA",
                            "Compensación: la venta no pudo confirmarse", lineas));
                    log.warn("Venta {} revertida: stock devuelto en ms-inventario", ventaId);
                } catch (Exception e) {
                    log.error("No se pudo devolver el stock de la venta {}. Revisar manualmente.", ventaId, e);
                }
            }
        });
    }
}
