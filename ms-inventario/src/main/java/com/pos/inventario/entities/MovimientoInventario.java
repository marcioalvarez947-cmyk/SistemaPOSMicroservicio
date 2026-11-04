package com.pos.inventario.entities;

import com.pos.inventario.enums.MotivoMovimiento;
import com.pos.inventario.enums.TipoMovimiento;
import com.pos.inventario.enums.TipoReferencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Kardex. Es inmutable: nunca se edita ni se borra, solo se agregan movimientos.
 * La restricción única hace que registrar dos veces el mismo documento sea imposible (idempotencia).
 */
@Entity
@Table(name = "movimiento_inventario",
        uniqueConstraints = @UniqueConstraint(name = "uk_movimiento_referencia",
                columnNames = {"producto_id", "referencia_tipo", "referencia_id", "motivo"}),
        indexes = @Index(name = "idx_movimiento_producto_fecha", columnList = "producto_id, fecha_movimiento"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoMovimiento tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MotivoMovimiento motivo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "stock_anterior", nullable = false)
    private Integer stockAnterior;

    @Column(name = "stock_posterior", nullable = false)
    private Integer stockPosterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "referencia_tipo", length = 15)
    private TipoReferencia referenciaTipo;

    /** Id de la venta u orden de compra. Nulo en los ajustes manuales. */
    @Column(name = "referencia_id")
    private Long referenciaId;

    @Column(length = 255)
    private String observacion;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;
}
