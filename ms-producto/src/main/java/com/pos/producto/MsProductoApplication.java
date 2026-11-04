package com.pos.producto;

import com.pos.producto.entities.Categoria;
import com.pos.producto.entities.Producto;
import com.pos.producto.enums.UnidadMedida;
import com.pos.producto.repositories.CategoriaRepository;
import com.pos.producto.repositories.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.List;

@SpringBootApplication
public class MsProductoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsProductoApplication.class, args);
    }

    /**
     * Puebla categorías y productos solo si las tablas están vacías.
     * IMPORTANTE: ms-inventario y ms-compras usan estos mismos ids (1 a 12) y códigos de barras en sus datos de prueba.
     */
    @Bean
    CommandLineRunner poblarProductos(CategoriaRepository categoriaRepository,
                                      ProductoRepository productoRepository) {
        return args -> {
            if (categoriaRepository.count() > 0 || productoRepository.count() > 0) {
                return;
            }
            Categoria bebidas = categoria("Bebidas", "Gaseosas, aguas y jugos");
            Categoria abarrotes = categoria("Abarrotes", "Productos de despensa");
            Categoria lacteos = categoria("Lácteos", "Leche, yogurt y derivados");
            Categoria limpieza = categoria("Limpieza", "Artículos de limpieza del hogar");
            Categoria snacks = categoria("Snacks", "Galletas y piqueos");
            categoriaRepository.saveAll(List.of(bebidas, abarrotes, lacteos, limpieza, snacks));

            productoRepository.saveAll(List.of(
                    producto("7750001000011", "BEB-001", "Gaseosa cola 500 ml", bebidas, "2.50", "1.60", UnidadMedida.UNIDAD),
                    producto("7750001000028", "BEB-002", "Agua mineral sin gas 625 ml", bebidas, "1.50", "0.80", UnidadMedida.UNIDAD),
                    producto("7750001000035", "BEB-003", "Jugo de naranja 1 L", bebidas, "5.50", "3.80", UnidadMedida.UNIDAD),
                    producto("7750001000042", "ABA-001", "Arroz extra 1 kg", abarrotes, "4.80", "3.70", UnidadMedida.UNIDAD),
                    producto("7750001000059", "ABA-002", "Azúcar rubia 1 kg", abarrotes, "4.20", "3.20", UnidadMedida.UNIDAD),
                    producto("7750001000066", "ABA-003", "Aceite vegetal 1 L", abarrotes, "9.90", "7.60", UnidadMedida.UNIDAD),
                    producto("7750001000073", "LAC-001", "Leche entera 1 L", lacteos, "4.90", "3.90", UnidadMedida.UNIDAD),
                    producto("7750001000080", "LAC-002", "Yogurt natural 1 L", lacteos, "7.50", "5.80", UnidadMedida.UNIDAD),
                    producto("7750001000097", "LIM-001", "Detergente en polvo 800 g", limpieza, "12.50", "9.40", UnidadMedida.UNIDAD),
                    producto("7750001000103", "LIM-002", "Lejía 1 L", limpieza, "4.50", "3.10", UnidadMedida.UNIDAD),
                    producto("7750001000110", "SNA-001", "Galletas de vainilla", snacks, "1.20", "0.80", UnidadMedida.PAQUETE),
                    producto("7750001000127", "SNA-002", "Papas fritas 150 g", snacks, "4.00", "2.70", UnidadMedida.UNIDAD)
            ));
        };
    }

    private static Categoria categoria(String nombre, String descripcion) {
        return Categoria.builder().nombre(nombre).descripcion(descripcion).build();
    }

    private static Producto producto(String codigoBarras, String sku, String nombre, Categoria categoria,
                                     String precioVenta, String costo, UnidadMedida unidad) {
        return Producto.builder()
                .codigoBarras(codigoBarras)
                .sku(sku)
                .nombre(nombre)
                .categoria(categoria)
                .unidadMedida(unidad)
                .precioVenta(new BigDecimal(precioVenta))
                .costoReferencial(new BigDecimal(costo))
                .build();
    }
}
