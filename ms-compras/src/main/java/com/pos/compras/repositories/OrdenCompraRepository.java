package com.pos.compras.repositories;

import com.pos.compras.entities.OrdenCompra;
import com.pos.compras.enums.EstadoOrdenCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdenCompraRepository extends JpaRepository<OrdenCompra, Long> {

    /** Trae orden + proveedor + líneas en una sola consulta. */
    @EntityGraph(attributePaths = {"proveedor", "detalles"})
    Optional<OrdenCompra> findWithDetallesById(Long id);

    @EntityGraph(attributePaths = "proveedor")
    Page<OrdenCompra> findByEstado(EstadoOrdenCompra estado, Pageable pageable);

    /** Listado completo trayendo el proveedor (evita N+1 al mostrar la razón social). */
    @EntityGraph(attributePaths = "proveedor")
    Page<OrdenCompra> findAllBy(Pageable pageable);

    boolean existsByNumeroOrden(String numeroOrden);
}
