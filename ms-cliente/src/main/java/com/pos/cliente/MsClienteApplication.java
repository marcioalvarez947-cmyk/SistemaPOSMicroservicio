package com.pos.cliente;

import com.pos.cliente.entities.Cliente;
import com.pos.cliente.enums.TipoDocumento;
import com.pos.cliente.repositories.ClienteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.List;

@SpringBootApplication
public class MsClienteApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsClienteApplication.class, args);
    }

    /** Puebla la tabla cliente solo si está vacía (se puede reiniciar sin duplicar datos). */
    @Bean
    CommandLineRunner poblarClientes(ClienteRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            repository.saveAll(List.of(
                    persona("40000001", "Carlos", "Mendoza Rojas", "carlos.mendoza@correo.com",
                            "987000001", "Av. Los Álamos 123", LocalDate.of(1988, 5, 14)),
                    persona("40000002", "María Fernanda", "Quispe Vargas", "maria.quispe@correo.com",
                            "987000002", "Jr. Amazonas 456", LocalDate.of(1992, 11, 2)),
                    persona("40000003", "Luis Alberto", "Torres Díaz", "luis.torres@correo.com",
                            "987000003", "Calle Los Pinos 78", LocalDate.of(1979, 3, 27)),
                    persona("40000004", "Ana Lucía", "Paredes Soto", "ana.paredes@correo.com",
                            "987000004", "Psje. Las Flores 12", LocalDate.of(1995, 8, 9)),
                    persona("40000005", "Jorge", "Salazar Linares", "jorge.salazar@correo.com",
                            "987000005", "Av. Perú 910", LocalDate.of(1985, 1, 30)),
                    empresa("20600000011", "Bodega El Sol", "Comercial El Sol S.A.C.",
                            "ventas@elsol.com", "976000011", "Mercado Central, puesto 25")
            ));
        };
    }

    private static Cliente persona(String dni, String nombres, String apellidos, String email,
                                   String telefono, String direccion, LocalDate nacimiento) {
        return Cliente.builder()
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento(dni)
                .nombres(nombres)
                .apellidos(apellidos)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .fechaNacimiento(nacimiento)
                .build();
    }

    private static Cliente empresa(String ruc, String nombres, String razonSocial, String email,
                                   String telefono, String direccion) {
        return Cliente.builder()
                .tipoDocumento(TipoDocumento.RUC)
                .numeroDocumento(ruc)
                .nombres(nombres)
                .razonSocial(razonSocial)
                .email(email)
                .telefono(telefono)
                .direccion(direccion)
                .build();
    }
}
