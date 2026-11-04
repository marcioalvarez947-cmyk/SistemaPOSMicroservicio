package com.pos.compras;

import com.pos.compras.entities.DetalleOrdenCompra;
import com.pos.compras.entities.OrdenCompra;
import com.pos.compras.entities.Proveedor;
import com.pos.compras.enums.EstadoOrdenCompra;
import com.pos.compras.repositories.OrdenCompraRepository;
import com.pos.compras.repositories.ProveedorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
public class MsComprasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsComprasApplication.class, args);
    }

    /**
     * Puebla 3 proveedores y una orden de compra PENDIENTE (lista para recepcionar en la demo).
     * Los productos de la orden son los ids 1, 2 y 12 que crea ms-producto.
     */
    @Bean
    CommandLineRunner poblarCompras(ProveedorRepository proveedorRepository,
                                    OrdenCompraRepository ordenRepository) {
        return args -> {
            if (proveedorRepository.count() > 0) {
                return;
            }
            List<Proveedor> proveedores = proveedorRepository.saveAll(List.of(
                    proveedor("20100000011", "Distribuidora Andina S.A.C.", "Andina Distribuciones",
                            "Rosa Gutiérrez", "976100011", "ventas@andina.com", "Av. Industrial 450"),
                    proveedor("20100000028", "Lácteos del Valle E.I.R.L.", "Lácteos del Valle",
                            "Pedro Cabrera", "976100028", "pedidos@lacteosvalle.com", "Carretera Norte km 12"),
                    proveedor("20100000035", "Importaciones Norte S.R.L.", "Importaciones Norte",
                            "Sandra Ríos", "976100035", "contacto@impnorte.com", "Jr. Comercio 233")
            ));

            OrdenCompra orden = OrdenCompra.builder()
                    .numeroOrden("OC-000001")
                    .proveedor(proveedores.get(0))
                    .fechaEmision(LocalDate.now())
                    .fechaEntregaEsperada(LocalDate.now().plusDays(3))
                    .estado(EstadoOrdenCompra.PENDIENTE)
                    .subtotal(BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .observaciones("Reposición inicial de bebidas y snacks")
                    .build();
            orden.agregarDetalle(detalle(1L, "7750001000011", "Gaseosa cola 500 ml", 24, "1.60"));
            orden.agregarDetalle(detalle(2L, "7750001000028", "Agua mineral sin gas 625 ml", 48, "0.80"));
            orden.agregarDetalle(detalle(12L, "7750001000127", "Papas fritas 150 g", 30, "2.70"));

            BigDecimal total = orden.getDetalles().stream()
                    .map(DetalleOrdenCompra::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orden.setSubtotal(total);
            orden.setTotal(total);
            ordenRepository.save(orden);
        };
    }

    private static Proveedor proveedor(String ruc, String razonSocial, String nombreComercial,
                                       String contacto, String telefono, String email, String direccion) {
        return Proveedor.builder()
                .ruc(ruc)
                .razonSocial(razonSocial)
                .nombreComercial(nombreComercial)
                .personaContacto(contacto)
                .telefono(telefono)
                .email(email)
                .direccion(direccion)
                .build();
    }

    private static DetalleOrdenCompra detalle(Long productoId, String codigoBarras, String nombre,
                                              int cantidad, String costo) {
        BigDecimal costoUnitario = new BigDecimal(costo);
        return DetalleOrdenCompra.builder()
                .productoId(productoId)
                .codigoBarras(codigoBarras)
                .nombreProducto(nombre)
                .cantidad(cantidad)
                .costoUnitario(costoUnitario)
                .subtotal(costoUnitario.multiply(BigDecimal.valueOf(cantidad)))
                .build();
    }
}
