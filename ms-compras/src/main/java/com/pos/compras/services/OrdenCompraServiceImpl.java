package com.pos.compras.services;

import com.pos.compras.clients.InventarioClient;
import com.pos.compras.clients.ProductoClient;
import com.pos.compras.dtos.LineaOrdenCompraRequest;
import com.pos.compras.dtos.OrdenCompraRequest;
import com.pos.compras.dtos.OrdenCompraResponse;
import com.pos.compras.dtos.OrdenCompraResumenResponse;
import com.pos.compras.dtos.PageResponse;
import com.pos.compras.dtos.remote.LineaMovimientoRequest;
import com.pos.compras.dtos.remote.MovimientoLoteRequest;
import com.pos.compras.dtos.remote.ProductoRemoto;
import com.pos.compras.entities.DetalleOrdenCompra;
import com.pos.compras.entities.OrdenCompra;
import com.pos.compras.entities.Proveedor;
import com.pos.compras.enums.EstadoOrdenCompra;
import com.pos.compras.exceptions.BusinessRuleException;
import com.pos.compras.exceptions.ResourceNotFoundException;
import com.pos.compras.repositories.OrdenCompraRepository;
import com.pos.compras.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrdenCompraServiceImpl implements OrdenCompraService {

    private final OrdenCompraRepository ordenRepository;
    private final ProveedorRepository proveedorRepository;
    private final OrdenCompraMapper mapper;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;

    @Override
    @Transactional
    public OrdenCompraResponse crear(OrdenCompraRequest request) {
        Proveedor proveedor = proveedorRepository.findById(request.proveedorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor no encontrado con id " + request.proveedorId()));
        if (!Boolean.TRUE.equals(proveedor.getActivo())) {
            throw new BusinessRuleException("El proveedor '%s' está inactivo".formatted(proveedor.getRazonSocial()));
        }

        List<DetalleOrdenCompra> detalles = resolverLineas(request.lineas());
        BigDecimal subtotal = detalles.stream().map(DetalleOrdenCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrdenCompra orden = OrdenCompra.builder()
                .numeroOrden("TMP-" + UUID.randomUUID().toString().substring(0, 12))
                .proveedor(proveedor)
                .fechaEmision(LocalDate.now())
                .fechaEntregaEsperada(request.fechaEntregaEsperada())
                .estado(EstadoOrdenCompra.PENDIENTE)
                .subtotal(subtotal)
                .total(subtotal)
                .observaciones(request.observaciones())
                .build();
        detalles.forEach(orden::agregarDetalle);

        orden = ordenRepository.saveAndFlush(orden);
        orden.setNumeroOrden("OC-%06d".formatted(orden.getId()));
        log.info("Orden de compra creada id={} numero={} total={}", orden.getId(), orden.getNumeroOrden(), subtotal);
        return mapper.toResponse(orden);
    }

    @Override
    public OrdenCompraResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarConDetalles(id));
    }

    @Override
    public PageResponse<OrdenCompraResumenResponse> listar(EstadoOrdenCompra estado, Pageable pageable) {
        Page<OrdenCompra> page = estado == null
                ? ordenRepository.findAllBy(pageable)
                : ordenRepository.findByEstado(estado, pageable);
        return PageResponse.from(page.map(mapper::toResumen));
    }

    @Override
    @Transactional
    public OrdenCompraResponse recepcionar(Long id) {
        OrdenCompra orden = buscarConDetalles(id);
        if (orden.getEstado() != EstadoOrdenCompra.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden recepcionar órdenes PENDIENTES (estado actual: %s)".formatted(orden.getEstado()));
        }

        // 1) Aumentar stock. Es idempotente en ms-inventario (referencia COMPRA + id de la orden):
        //    si algo falla después y se reintenta, el stock NO se duplica.
        List<LineaMovimientoRequest> lineas = orden.getDetalles().stream()
                .map(d -> new LineaMovimientoRequest(d.getProductoId(), d.getCantidad()))
                .toList();
        inventarioClient.registrarEntradas(new MovimientoLoteRequest(
                "COMPRA", orden.getId(), "COMPRA", "Recepción de la orden " + orden.getNumeroOrden(), lineas));

        // 2) Solo si inventario respondió bien, la orden pasa a RECEPCIONADA.
        orden.setEstado(EstadoOrdenCompra.RECEPCIONADA);
        orden.setFechaRecepcion(LocalDateTime.now());
        log.info("Orden {} recepcionada: stock actualizado", orden.getNumeroOrden());
        return mapper.toResponse(ordenRepository.save(orden));
    }

    @Override
    @Transactional
    public OrdenCompraResponse anular(Long id, String motivo) {
        OrdenCompra orden = buscarConDetalles(id);
        if (orden.getEstado() != EstadoOrdenCompra.PENDIENTE) {
            throw new BusinessRuleException(
                    "Solo se pueden anular órdenes PENDIENTES (estado actual: %s)".formatted(orden.getEstado()));
        }
        orden.setEstado(EstadoOrdenCompra.ANULADA);
        orden.setMotivoAnulacion(motivo);
        return mapper.toResponse(ordenRepository.save(orden));
    }

    // ---------------------------------------------------------------- helpers

    private List<DetalleOrdenCompra> resolverLineas(List<LineaOrdenCompraRequest> lineas) {
        Set<Long> vistos = new HashSet<>();
        List<DetalleOrdenCompra> detalles = new ArrayList<>();
        for (LineaOrdenCompraRequest linea : lineas) {
            if (!vistos.add(linea.productoId())) {
                throw new BusinessRuleException(
                        "El producto %d está repetido en la orden".formatted(linea.productoId()));
            }
            ProductoRemoto producto = productoClient.obtenerPorId(linea.productoId());
            if (!Boolean.TRUE.equals(producto.activo())) {
                throw new BusinessRuleException("El producto '%s' está inactivo".formatted(producto.nombre()));
            }
            detalles.add(DetalleOrdenCompra.builder()
                    .productoId(producto.id())
                    .codigoBarras(producto.codigoBarras())
                    .nombreProducto(producto.nombre())
                    .cantidad(linea.cantidad())
                    .costoUnitario(linea.costoUnitario().setScale(2, RoundingMode.HALF_UP))
                    .subtotal(linea.costoUnitario()
                            .multiply(BigDecimal.valueOf(linea.cantidad()))
                            .setScale(2, RoundingMode.HALF_UP))
                    .build());
        }
        return detalles;
    }

    private OrdenCompra buscarConDetalles(Long id) {
        return ordenRepository.findWithDetallesById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Orden de compra no encontrada con id " + id));
    }
}
