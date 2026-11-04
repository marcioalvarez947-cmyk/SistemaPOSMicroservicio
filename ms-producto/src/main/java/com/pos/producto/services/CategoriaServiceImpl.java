package com.pos.producto.services;

import com.pos.producto.dtos.CategoriaRequest;
import com.pos.producto.dtos.CategoriaResponse;
import com.pos.producto.entities.Categoria;
import com.pos.producto.exceptions.DuplicateResourceException;
import com.pos.producto.exceptions.ResourceNotFoundException;
import com.pos.producto.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository repository;
    private final CategoriaMapper mapper;

    @Override
    public List<CategoriaResponse> listar() {
        return repository.findAll(Sort.by("nombre")).stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public CategoriaResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public CategoriaResponse crear(CategoriaRequest request) {
        String nombre = request.nombre().trim();
        if (repository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException("Ya existe la categoría '" + nombre + "'");
        }
        Categoria guardada = repository.save(mapper.toEntity(request));
        log.info("Categoría creada id={}", guardada.getId());
        return mapper.toResponse(guardada);
    }

    @Override
    @Transactional
    public CategoriaResponse actualizar(Long id, CategoriaRequest request) {
        Categoria categoria = buscarEntidad(id);
        String nombre = request.nombre().trim();
        if (!categoria.getNombre().equalsIgnoreCase(nombre) && repository.existsByNombreIgnoreCase(nombre)) {
            throw new DuplicateResourceException("Ya existe la categoría '" + nombre + "'");
        }
        mapper.updateEntity(categoria, request);
        return mapper.toResponse(repository.save(categoria));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Categoria categoria = buscarEntidad(id);
        categoria.setActivo(false);
        repository.save(categoria);
    }

    private Categoria buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + id));
    }
}
