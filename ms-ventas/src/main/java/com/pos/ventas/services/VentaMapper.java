package com.pos.ventas.services;

import com.pos.ventas.dtos.DetalleVentaResponse;
import com.pos.ventas.dtos.VentaResponse;
import com.pos.ventas.dtos.VentaResumenResponse;
import com.pos.ventas.entities.DetalleVenta;
import com.pos.ventas.entities.Venta;
import org.springframework.stereotype.Component;

@Component
public class VentaMapper {

    public VentaResponse toResponse(Venta v) {
        return new VentaResponse(
                v.getId(),
                v.getNumeroTicket(),
                v.getFechaVenta(),
                v.getClienteId(),
                v.getMetodoPago(),
                v.getSubtotal(),
                v.getDescuento(),
                v.getTotal(),
                v.getMontoRecibido(),
                v.getVuelto(),
                v.getEstado(),
                v.getObservacion(),
                v.getMotivoAnulacion(),
                v.getFechaAnulacion(),
                v.getDetalles().stream().map(this::toResponse).toList());
    }

    public DetalleVentaResponse toResponse(DetalleVenta d) {
        return new DetalleVentaResponse(d.getId(), d.getProductoId(), d.getCodigoBarras(),
                d.getNombreProducto(), d.getCantidad(), d.getPrecioUnitario(), d.getDescuento(),
                d.getSubtotal());
    }

    public VentaResumenResponse toResumen(Venta v) {
        return new VentaResumenResponse(v.getId(), v.getNumeroTicket(), v.getFechaVenta(),
                v.getClienteId(), v.getMetodoPago(), v.getTotal(), v.getEstado());
    }
}
