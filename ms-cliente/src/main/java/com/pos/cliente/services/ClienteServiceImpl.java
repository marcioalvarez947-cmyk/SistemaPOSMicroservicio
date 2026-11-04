package com.pos.cliente.services;

import com.pos.cliente.dtos.ClienteRequest;
import com.pos.cliente.dtos.ClienteResponse;
import com.pos.cliente.dtos.PageResponse;
import com.pos.cliente.entities.Cliente;
import com.pos.cliente.exceptions.DuplicateResourceException;
import com.pos.cliente.exceptions.ResourceNotFoundException;
import com.pos.cliente.repositories.ClienteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> listar(String texto, Pageable pageable) {
        Page<Cliente> page = (texto == null || texto.isBlank())
                ? repository.findByActivoTrue(pageable)
                : repository.buscar(texto.trim(), pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id) {
        return mapper.toResponse(buscarEntidad(id));
    }

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        String documento = request.numeroDocumento().trim();
        if (repository.existsByTipoDocumentoAndNumeroDocumento(request.tipoDocumento(), documento)) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente con %s %s".formatted(request.tipoDocumento(), documento));
        }
        Cliente guardado = repository.save(mapper.toEntity(request));
        log.info("Cliente creado id={}", guardado.getId());
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        Cliente cliente = buscarEntidad(id);
        String documento = request.numeroDocumento().trim();

        boolean cambioDocumento = cliente.getTipoDocumento() != request.tipoDocumento()
                || !cliente.getNumeroDocumento().equals(documento);
        if (cambioDocumento
                && repository.existsByTipoDocumentoAndNumeroDocumento(request.tipoDocumento(), documento)) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente con %s %s".formatted(request.tipoDocumento(), documento));
        }

        mapper.updateEntity(cliente, request);
        return mapper.toResponse(repository.save(cliente));
    }

    @Override
    @Transactional
    public void desactivar(Long id) {
        Cliente cliente = buscarEntidad(id);
        cliente.setActivo(false);
        repository.save(cliente);
        log.info("Cliente desactivado id={}", id);
    }

    private Cliente buscarEntidad(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + id));
    }
}
