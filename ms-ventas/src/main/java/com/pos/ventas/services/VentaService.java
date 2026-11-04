package com.pos.ventas.services;

import com.pos.ventas.dtos.PageResponse;
import com.pos.ventas.dtos.VentaRequest;
import com.pos.ventas.dtos.VentaResponse;
import com.pos.ventas.dtos.VentaResumenResponse;
import com.pos.ventas.enums.EstadoVenta;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface VentaService {

    VentaResponse crear(VentaRequest request);

    VentaResponse obtenerPorId(Long id);

    PageResponse<VentaResumenResponse> listar(EstadoVenta estado, LocalDate desde, LocalDate hasta,
                                              Pageable pageable);

    VentaResponse anular(Long id, String motivo);
}
