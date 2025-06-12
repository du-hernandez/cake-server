package com.altico.cakeserver.applications.ports.output;

import com.altico.cakeserver.domain.model.UsuarioAuditoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida para la persistencia de Auditoría
 */
public interface AuditoriaPersistencePort {

    // Operaciones CRUD básicas
    UsuarioAuditoria save(UsuarioAuditoria auditoria);
    Optional<UsuarioAuditoria> findById(Long id);
    void deleteById(Long id);

    // Búsquedas por usuario
    List<UsuarioAuditoria> findByUsuarioId(Long usuarioId);
    Page<UsuarioAuditoria> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Búsquedas por acción y fecha
    List<UsuarioAuditoria> findByAccion(String accion);
    List<UsuarioAuditoria> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Page<UsuarioAuditoria> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    // Búsquedas por IP y User Agent
    List<UsuarioAuditoria> findByIpAddress(String ipAddress);
    List<UsuarioAuditoria> findSuspiciousActivity(LocalDateTime desde);

    // Operaciones de limpieza
    void deleteOlderThan(LocalDateTime fecha);
    long countByResultado(String resultado);

    // Estadísticas
    List<Object[]> getAuditoriaEstadisticas(LocalDateTime desde, LocalDateTime hasta);
}
