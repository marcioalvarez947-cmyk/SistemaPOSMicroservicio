package com.pos.ventas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * ms-ventas no se puebla desde aquí: una venta necesita productos, clientes y stock reales
 * de los otros servicios, así que las ventas se generan por la API (Postman) durante la demo.
 */
@SpringBootApplication
@EnableFeignClients
public class MsVentasApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsVentasApplication.class, args);
    }
}
