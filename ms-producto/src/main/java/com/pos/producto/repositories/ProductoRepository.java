package com.pos.producto.repositories;

import com.pos.producto.entities.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    /** Búsqueda que usa la caja al escanear. */
    @EntityGraph(attributePaths = "categoria")
    Optional<Producto> findByCodigoBarrasAndActivoTrue(String codigoBarras);

    boolean existsByCodigoBarras(String codigoBarras);

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = "categoria")
    Page<Producto> findByActivoTrue(Pageable pageable);

    @EntityGraph(attributePaths = "categoria")
    @Query("""
            SELECT p FROM Producto p
            WHERE p.activo = true
              AND (LOWER(p.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR p.codigoBarras LIKE CONCAT('%', :texto, '%')
                   OR LOWER(p.sku) LIKE LOWER(CONCAT('%', :texto, '%')))
            """)
    Page<Producto> buscar(@Param("texto") String texto, Pageable pageable);
}
