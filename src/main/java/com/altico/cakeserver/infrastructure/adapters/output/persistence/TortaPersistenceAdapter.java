package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.TortaPersistencePort;
import com.altico.cakeserver.domain.model.Torta;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.TortaPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class TortaPersistenceAdapter implements TortaPersistencePort {

    private final TortaRepository tortaRepository;
    private final OcasionRepository ocasionRepository;
    private final TortaPersistenceMapper mapper;

    public TortaPersistenceAdapter(TortaRepository tortaRepository,
                                   OcasionRepository ocasionRepository,
                                   TortaPersistenceMapper mapper) {
        this.tortaRepository = tortaRepository;
        this.ocasionRepository = ocasionRepository;
        this.mapper = mapper;
    }

    @Override
    public Torta save(Torta torta) {
        TortaEntity entity = mapper.toEntity(torta);
        TortaEntity saved = tortaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torta> findById(Integer id) {
        return tortaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Torta> findByIdWithRelations(Integer id) {
        return tortaRepository.findByIdWithOcasiones(id)
                .map(mapper::toDomainWithRelations);
    }

    @Override
    public void deleteById(Integer id) {
        tortaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Integer id) {
        return tortaRepository.existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> findAll() {
        return tortaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Torta> findAll(Pageable pageable) {
        return tortaRepository.findAll(pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> findByDescripcionContaining(String descripcion) {
        return tortaRepository.searchByDescripcion(descripcion).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> findByOcasionId(Integer ocasionId) {
        return tortaRepository.findByOcasionId(ocasionId, Pageable.unpaged())
                .getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> findByOcasionIds(List<Integer> ocasionIds) {
        return tortaRepository.findByOcasionIds(ocasionIds).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> findRecent(int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit,
                Sort.by(Sort.Direction.DESC, "fechaCreado"));
        return tortaRepository.findAll(pageRequest)
                .getContent().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long count() {
        return tortaRepository.count();
    }

    @Override
    public void addOcasion(Integer tortaId, Integer ocasionId) {
        TortaEntity torta = tortaRepository.findById(tortaId)
                .orElseThrow(() -> new IllegalArgumentException("Torta no encontrada"));
        OcasionEntity ocasion = ocasionRepository.findById(ocasionId)
                .orElseThrow(() -> new IllegalArgumentException("Ocasión no encontrada"));

        torta.addOcasion(ocasion, true);
        tortaRepository.save(torta);
    }

    @Override
    public void removeOcasion(Integer tortaId, Integer ocasionId) {
        TortaEntity torta = tortaRepository.findById(tortaId)
                .orElseThrow(() -> new IllegalArgumentException("Torta no encontrada"));
        OcasionEntity ocasion = ocasionRepository.findById(ocasionId)
                .orElseThrow(() -> new IllegalArgumentException("Ocasión no encontrada"));

        torta.removeOcasion(ocasion);
        tortaRepository.save(torta);
    }

    @Override
    public void updateOcasionStatus(Integer tortaId, Integer ocasionId, boolean activo) {
        TortaEntity torta = tortaRepository.findById(tortaId)
                .orElseThrow(() -> new IllegalArgumentException("Torta no encontrada"));

        torta.getTortaOcasiones().stream()
                .filter(to -> to.getOcasion().getId().equals(ocasionId))
                .findFirst()
                .ifPresent(to -> to.setEstado(activo ? (byte) 1 : (byte) 0));

        tortaRepository.save(torta);
    }
}
