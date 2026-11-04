package com.pos.inventario.repositories;

import com.pos.inventario.entities.MovimientoInventario;
import com.pos.inventario.enums.MotivoMovimiento;
import com.pos.inventario.enums.TipoReferencia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovimientoInventarioRepository extends JpaRepository<MovimientoInventario, Long> {

    /** Para la idempotencia: ¿ya registré este documento para este producto? */
    boolean existsByProductoIdAndReferenciaTipoAndReferenciaIdAndMotivo(
            Long productoId, TipoReferencia referenciaTipo, Long referenciaId, MotivoMovimiento motivo);

    Page<MovimientoInventario> findByProductoIdOrderByFechaMovimientoDesc(Long productoId, Pageable pageable);
}
