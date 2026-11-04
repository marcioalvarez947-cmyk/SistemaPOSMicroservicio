package com.pos.inventario.repositories;

import com.pos.inventario.entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    Optional<Stock> findByProductoId(Long productoId);

    @Query("SELECT s FROM Stock s WHERE s.cantidad <= s.stockMinimo")
    List<Stock> findConStockBajo();

    /**
     * Descuento ATÓMICO: resta solo si alcanza el stock. Devuelve 0 filas afectadas si no alcanza,
     * así dos cajas que venden la última unidad a la vez no dejan el stock en negativo.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Stock s SET s.cantidad = s.cantidad - :cantidad
            WHERE s.productoId = :productoId AND s.cantidad >= :cantidad
            """)
    int descontar(@Param("productoId") Long productoId, @Param("cantidad") int cantidad);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Stock s SET s.cantidad = s.cantidad + :cantidad WHERE s.productoId = :productoId")
    int incrementar(@Param("productoId") Long productoId, @Param("cantidad") int cantidad);
}
