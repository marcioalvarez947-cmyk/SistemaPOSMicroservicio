package com.pos.producto.services;

import com.pos.producto.dtos.ProductoRequest;
import com.pos.producto.dtos.ProductoResponse;
import com.pos.producto.entities.Categoria;
import com.pos.producto.entities.Producto;
import com.pos.producto.enums.UnidadMedida;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

    public Producto toEntity(ProductoRequest r, Categoria categoria) {
        Producto p = new Producto();
        p.setActivo(true);
        aplicar(p, r, categoria);
        return p;
    }

    public void updateEntity(Producto p, ProductoRequest r, Categoria categoria) {
        aplicar(p, r, categoria);
    }

    private void aplicar(Producto p, ProductoRequest r, Categoria categoria) {
        p.setCodigoBarras(r.codigoBarras().trim());
        p.setSku(r.sku() == null || r.sku().isBlank() ? null : r.sku().trim());
        p.setNombre(r.nombre().trim());
        p.setDescripcion(r.descripcion());
        p.setCategoria(categoria);
        p.setMarca(r.marca());
        p.setUnidadMedida(r.unidadMedida() == null ? UnidadMedida.UNIDAD : r.unidadMedida());
        p.setPrecioVenta(r.precioVenta());
        p.setCostoReferencial(r.costoReferencial());
        p.setImagenUrl(r.imagenUrl());
    }

    public ProductoResponse toResponse(Producto p) {
        return new ProductoResponse(
                p.getId(),
                p.getCodigoBarras(),
                p.getSku(),
                p.getNombre(),
                p.getDescripcion(),
                p.getCategoria().getId(),
                p.getCategoria().getNombre(),
                p.getMarca(),
                p.getUnidadMedida(),
                p.getPrecioVenta(),
                p.getCostoReferencial(),
                p.getImagenUrl(),
                p.getActivo(),
                p.getFechaCreacion(),
                p.getFechaActualizacion());
    }
}
