package com.pos.inventario;

import com.pos.inventario.entities.MovimientoInventario;
import com.pos.inventario.entities.Stock;
import com.pos.inventario.enums.MotivoMovimiento;
import com.pos.inventario.enums.TipoMovimiento;
import com.pos.inventario.enums.TipoReferencia;
import com.pos.inventario.repositories.MovimientoInventarioRepository;
import com.pos.inventario.repositories.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class MsInventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsInventarioApplication.class, args);
    }

    /**
     * Stock inicial de los 12 productos que crea ms-producto (ids 1 a 12).
     * Cada fila es {productoId, cantidad, stockMinimo}. El producto 12 queda con stock bajo a propósito.
     */
    @Bean
    CommandLineRunner poblarInventario(StockRepository stockRepository,
                                       MovimientoInventarioRepository movimientoRepository) {
        return args -> {
            if (stockRepository.count() > 0) {
                return;
            }
            int[][] datos = {
                    {1, 50, 10}, {2, 80, 15}, {3, 30, 8}, {4, 40, 10},
                    {5, 35, 10}, {6, 25, 5}, {7, 45, 10}, {8, 20, 5},
                    {9, 15, 4}, {10, 30, 6}, {11, 100, 20}, {12, 8, 10}
            };
            for (int[] d : datos) {
                long productoId = d[0];
                int cantidad = d[1];

                stockRepository.save(Stock.builder()
                        .productoId(productoId)
                        .cantidad(cantidad)
                        .stockMinimo(d[2])
                        .ubicacion("Almacén principal")
                        .build());

                // El kardex también refleja el stock inicial.
                movimientoRepository.save(MovimientoInventario.builder()
                        .productoId(productoId)
                        .tipo(TipoMovimiento.ENTRADA)
                        .motivo(MotivoMovimiento.AJUSTE)
                        .cantidad(cantidad)
                        .stockAnterior(0)
                        .stockPosterior(cantidad)
                        .referenciaTipo(TipoReferencia.AJUSTE)
                        .observacion("Stock inicial")
                        .fechaMovimiento(LocalDateTime.now())
                        .build());
            }
        };
    }
}
