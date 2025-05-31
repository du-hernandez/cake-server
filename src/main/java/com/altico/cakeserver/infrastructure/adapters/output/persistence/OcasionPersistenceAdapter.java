package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.OcasionPersistencePort;
import com.altico.cakeserver.domain.model.Ocasion;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.OcasionEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.OcasionPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.OcasionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Transactional
public class OcasionPersistenceAdapter implements OcasionPersistencePort {

    private final OcasionRepository ocasionRepository;
    private final OcasionPersistenceMapper mapper;

    public OcasionPersistenceAdapter(OcasionRepository ocasionRepository,
                                     OcasionPersistenceMapper mapper) {
        this.ocasionRepository = ocasionRepository;
        this.mapper = mapper;
    }

    @Override
    public Ocasion save(Ocasion ocasion) {
        OcasionEntity entity = mapper.toEntity(ocasion);
        OcasionEntity saved = ocasionRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ocasion> findById(Integer id) {
        return ocasionRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        ocasionRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return ocasionRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> findAll() {
        return ocasionRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ocasion> findAll(Pageable pageable) {
        return ocasionRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> findByEstado(boolean activo) {
        byte estado = activo ? (byte) 1 : (byte) 0;
        return ocasionRepository.findByEstado(estado).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> findByNombreContaining(String nombre) {
        return ocasionRepository.buscarPorNombre(nombre).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Ocasion> findByNombre(String nombre) {
        return ocasionRepository.findByNombreIgnoreCase(nombre)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNombre(String nombre) {
        return ocasionRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findMostPopular(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        List<Object[]> results = ocasionRepository.findOcasionesMasPopulares(pageRequest);

        return results.stream()
                .map(row -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", row[0]);
                    map.put("nombre", row[1]);
                    map.put("cantidadTortas", row[2]);
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> findWithoutTortas() {
        return ocasionRepository.findOcasionesSinTortas().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countByEstado(boolean activo) {
        byte estado = activo ? (byte) 1 : (byte) 0;
        return ocasionRepository.findByEstado(estado).size();
    }
}