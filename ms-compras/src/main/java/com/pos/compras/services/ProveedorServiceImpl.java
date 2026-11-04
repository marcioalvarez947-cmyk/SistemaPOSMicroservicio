package com.pos.compras.services;

import com.pos.compras.dtos.PageResponse;
import com.pos.compras.dtos.ProveedorRequest;
import com.pos.compras.dtos.ProveedorResponse;
import com.pos.compras.entities.Proveedor;
import com.pos.compras.exceptions.DuplicateResourceException;
import com.pos.compras.exceptions.ResourceNotFoundException;
import com.pos.compras.repositories.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository repository;
    private final ProveedorMapper mapper;

    @Override
    public PageResponse<ProveedorResponse> listar(Pageable pageable) {
        return PageResponse.from(repository.findByActivoTrue(pageable).map(mapper::toResponse));
    }

    @Override
    public ProveedorResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        String ruc = request.ruc().trim();
        if (repository.existsByRuc(ruc)) {
            throw new DuplicateResourceException("Ya existe un proveedor con RUC " + ruc);
        }
        Proveedor guardado = repository.save(mapper.toEntity(request));
        log.info("Proveedor creado id={}", guardado.getId());
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Long id, ProveedorRequest request) {
        Proveedor proveedor = buscarEntidad(id);
        String ruc = request.ruc().trim();
        if (!proveedor.getRuc().equals(ruc) && repository.existsByRuc(ruc)) {
            throw new DuplicateResourceException("Ya existe un proveedor con RUC " + ruc);
        }
        mapper.updateEntity(proveedor, request);
        return mapper.toResponse(repository.save(proveedor));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Proveedor proveedor = buscarEntidad(id);
        proveedor.setActivo(false);
        repository.save(proveedor);
    }

    private Proveedor buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id " + id));
    }
}
