package com.pos.producto.services;

import com.pos.producto.dtos.PageResponse;
import com.pos.producto.dtos.ProductoRequest;
import com.pos.producto.dtos.ProductoResponse;
import com.pos.producto.entities.Categoria;
import com.pos.producto.entities.Producto;
import com.pos.producto.exceptions.BusinessRuleException;
import com.pos.producto.exceptions.DuplicateResourceException;
import com.pos.producto.exceptions.ResourceNotFoundException;
import com.pos.producto.repositories.CategoriaRepository;
import com.pos.producto.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper mapper;

    @Override
    public PageResponse<ProductoResponse> listar(String texto, Pageable pageable) {
        Page<Producto> page = (texto == null || texto.isBlank())
                ? repository.findByActivoTrue(pageable)
                : repository.buscar(texto.trim(), pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Override
    public ProductoResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarEntidad(id));
    }

    @Override
    public ProductoResponse obtenerPorCodigoBarras(String codigoBarras) {
        return repository.findByCodigoBarrasAndActivoTrue(codigoBarras.trim())
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe un producto activo con código de barras " + codigoBarras));
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        String codigo = request.codigoBarras().trim();
        if (repository.existsByCodigoBarras(codigo)) {
            throw new DuplicateResourceException("Ya existe un producto con código de barras " + codigo);
        }
        validarSkuDisponible(request.sku(), null);
        Categoria categoria = buscarCategoriaActiva(request.categoriaId());

        Producto guardado = repository.save(mapper.toEntity(request, categoria));
        log.info("Producto creado id={} codigo={}", guardado.getId(), codigo);
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Long id, ProductoRequest request) {
        Producto producto = buscarEntidad(id);
        String codigo = request.codigoBarras().trim();

        if (!producto.getCodigoBarras().equals(codigo) && repository.existsByCodigoBarras(codigo)) {
            throw new DuplicateResourceException("Ya existe un producto con código de barras " + codigo);
        }
        validarSkuDisponible(request.sku(), producto);
        Categoria categoria = buscarCategoriaActiva(request.categoriaId());

        mapper.updateEntity(producto, request, categoria);
        return mapper.toResponse(repository.save(producto));
    }

    @Override
    @Transactional
    public ProductoResponse cambiarCategoria(Long id, Long categoriaId) {
        Producto producto = buscarEntidad(id);
        producto.setCategoria(buscarCategoriaActiva(categoriaId));
        return mapper.toResponse(repository.save(producto));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Producto producto = buscarEntidad(id);
        producto.setActivo(false);
        repository.save(producto);
        log.info("Producto desactivado id={}", id);
    }

    @Override
    @Transactional
    public ProductoResponse activar(Long id) {
        Producto producto = buscarEntidad(id);
        producto.setActivo(true);
        return mapper.toResponse(repository.save(producto));
    }

    // ---------- helpers ----------

    private Producto buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id " + id));
    }

    private Categoria buscarCategoriaActiva(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id " + categoriaId));
        if (!Boolean.TRUE.equals(categoria.getActivo())) {
            throw new BusinessRuleException("La categoría '" + categoria.getNombre() + "' está inactiva");
        }
        return categoria;
    }

    private void validarSkuDisponible(String sku, Producto actual) {
        if (sku == null || sku.isBlank()) {
            return;
        }
        String limpio = sku.trim();
        boolean mismoSku = actual != null && limpio.equals(actual.getSku());
        if (!mismoSku && repository.existsBySku(limpio)) {
            throw new DuplicateResourceException("Ya existe un producto con SKU " + limpio);
        }
    }
}
