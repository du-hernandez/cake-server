package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.AuditoriaPersistencePort;
import com.altico.cakeserver.domain.model.UsuarioAuditoria;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.AuditoriaEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.AdminPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.AuditoriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditoriaPersistenceAdapter implements AuditoriaPersistencePort {

    private final AuditoriaRepository auditoriaRepository;
    private final AdminPersistenceMapper mapper;

    @Override
    public UsuarioAuditoria save(UsuarioAuditoria auditoria) {
        try {
            AuditoriaEntity entity = mapper.toEntity(auditoria);
            AuditoriaEntity saved = auditoriaRepository.save(entity);
            return mapper.toDomain(saved);
        } catch (Exception e) {
            log.error("Error guardando auditoría: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UsuarioAuditoria> findById(Long id) {
        return auditoriaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        auditoriaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> findByUsuarioId(Long usuarioId) {
        return auditoriaRepository.findByUsuarioId(usuarioId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioAuditoria> findByUsuarioId(Long usuarioId, Pageable pageable) {
        return auditoriaRepository.findByUsuarioId(usuarioId, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> findByAccion(String accion) {
        return auditoriaRepository.findByAccion(accion).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return auditoriaRepository.findByFechaBetween(fechaInicio, fechaFin).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioAuditoria> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable) {
        return auditoriaRepository.findByFechaBetween(fechaInicio, fechaFin, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> findByIpAddress(String ipAddress) {
        return auditoriaRepository.findByIpAddress(ipAddress).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioAuditoria> findSuspiciousActivity(LocalDateTime desde) {
        return auditoriaRepository.findSuspiciousActivity(desde).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOlderThan(LocalDateTime fecha) {
        auditoriaRepository.deleteByFechaBefore(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByResultado(String resultado) {
        return auditoriaRepository.countByResultado(resultado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getAuditoriaEstadisticas(LocalDateTime desde, LocalDateTime hasta) {
        return auditoriaRepository.getEstadisticas(desde, hasta);
    }
}
