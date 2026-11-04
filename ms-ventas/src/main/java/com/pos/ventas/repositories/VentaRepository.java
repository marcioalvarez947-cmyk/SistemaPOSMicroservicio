package com.pos.ventas.repositories;

import com.pos.ventas.entities.Venta;
import com.pos.ventas.enums.EstadoVenta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    /** Trae la venta con sus líneas en una sola consulta (evita el problema N+1). */
    @EntityGraph(attributePaths = "detalles")
    Optional<Venta> findWithDetallesById(Long id);

    Page<Venta> findByEstado(EstadoVenta estado, Pageable pageable);

    Page<Venta> findByFechaVentaBetween(LocalDateTime desde, LocalDateTime hasta, Pageable pageable);

    Page<Venta> findByEstadoAndFechaVentaBetween(EstadoVenta estado, LocalDateTime desde,
                                                 LocalDateTime hasta, Pageable pageable);

    boolean existsByNumeroTicket(String numeroTicket);
}
