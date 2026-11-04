package com.pos.compras.services;

import com.pos.compras.dtos.DetalleOrdenCompraResponse;
import com.pos.compras.dtos.OrdenCompraResponse;
import com.pos.compras.dtos.OrdenCompraResumenResponse;
import com.pos.compras.entities.DetalleOrdenCompra;
import com.pos.compras.entities.OrdenCompra;
import org.springframework.stereotype.Component;

@Component
public class OrdenCompraMapper {

    public OrdenCompraResponse toResponse(OrdenCompra o) {
        return new OrdenCompraResponse(
                o.getId(),
                o.getNumeroOrden(),
                o.getProveedor().getId(),
                o.getProveedor().getRazonSocial(),
                o.getFechaEmision(),
                o.getFechaEntregaEsperada(),
                o.getFechaRecepcion(),
                o.getEstado(),
                o.getSubtotal(),
                o.getTotal(),
                o.getObservaciones(),
                o.getMotivoAnulacion(),
                o.getDetalles().stream().map(this::toResponse).toList());
    }

    public DetalleOrdenCompraResponse toResponse(DetalleOrdenCompra d) {
        return new DetalleOrdenCompraResponse(d.getId(), d.getProductoId(), d.getCodigoBarras(),
                d.getNombreProducto(), d.getCantidad(), d.getCostoUnitario(), d.getSubtotal());
    }

    public OrdenCompraResumenResponse toResumen(OrdenCompra o) {
        return new OrdenCompraResumenResponse(o.getId(), o.getNumeroOrden(),
                o.getProveedor().getRazonSocial(), o.getFechaEmision(), o.getFechaRecepcion(),
                o.getEstado(), o.getTotal());
    }
}
