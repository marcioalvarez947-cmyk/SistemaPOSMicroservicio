package com.pos.producto.services;

import com.pos.producto.dtos.PageResponse;
import com.pos.producto.dtos.ProductoRequest;
import com.pos.producto.dtos.ProductoResponse;
import org.springframework.data.domain.Pageable;

public interface ProductoService {

    PageResponse<ProductoResponse> listar(String texto, Pageable pageable);

    ProductoResponse obtenerPorId(Long id);

    /** Lo que usa la caja al escanear un código. */
    ProductoResponse obtenerPorCodigoBarras(String codigoBarras);

    ProductoResponse crear(ProductoRequest request);

    ProductoResponse actualizar(Long id, ProductoRequest request);

    /** Vincula (reasigna) el producto a otra categoría. */
    ProductoResponse cambiarCategoria(Long id, Long categoriaId);

    void desactivar(Long id);

    ProductoResponse activar(Long id);
}
