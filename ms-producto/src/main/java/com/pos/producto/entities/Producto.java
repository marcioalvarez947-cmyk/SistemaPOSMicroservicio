package com.pos.producto.entities;

import com.pos.producto.enums.UnidadMedida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "producto",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_producto_codigo_barras", columnNames = "codigo_barras"),
                @UniqueConstraint(name = "uk_producto_sku", columnNames = "sku")
        },
        indexes = @Index(name = "idx_producto_nombre", columnList = "nombre"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Es lo que lee el escáner en la caja. */
    @Column(name = "codigo_barras", nullable = false, length = 50)
    private String codigoBarras;

    @Column(length = 40)
    private String sku;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_producto_categoria"))
    private Categoria categoria;

    @Column(length = 80)
    private String marca;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false, length = 20)
    private UnidadMedida unidadMedida = UnidadMedida.UNIDAD;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    /** Costo de referencia; el costo real de cada compra vive en ms-compras. */
    @Column(name = "costo_referencial", precision = 12, scale = 2)
    private BigDecimal costoReferencial;

    @Column(name = "imagen_url", length = 300)
    private String imagenUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean activo = true;
}
