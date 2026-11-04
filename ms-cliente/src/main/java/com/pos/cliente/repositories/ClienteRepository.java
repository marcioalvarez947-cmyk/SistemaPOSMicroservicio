package com.pos.cliente.repositories;

import com.pos.cliente.entities.Cliente;
import com.pos.cliente.enums.TipoDocumento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);

    Page<Cliente> findByActivoTrue(Pageable pageable);

    @Query("""
            SELECT c FROM Cliente c
            WHERE c.activo = true
              AND (LOWER(c.nombres) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(c.apellidos) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR c.numeroDocumento LIKE CONCAT('%', :texto, '%'))
            """)
    Page<Cliente> buscar(@Param("texto") String texto, Pageable pageable);
}
