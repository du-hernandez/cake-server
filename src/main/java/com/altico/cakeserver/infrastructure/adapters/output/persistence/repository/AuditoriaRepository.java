package com.altico.cakeserver.infrastructure.adapters.output.persistence.repository;

import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.AuditoriaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaRepository extends JpaRepository<AuditoriaEntity, Long> {

    // Búsquedas por usuario
    List<AuditoriaEntity> findByUsuarioId(Long usuarioId);
    Page<AuditoriaEntity> findByUsuarioId(Long usuarioId, Pageable pageable);

    // Búsquedas por acción
    List<AuditoriaEntity> findByAccion(String accion);

    // Búsquedas por fecha
    List<AuditoriaEntity> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Page<AuditoriaEntity> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin, Pageable pageable);

    // Búsquedas por IP
    List<AuditoriaEntity> findByIpAddress(String ipAddress);

    // Actividad sospechosa
    @Query("SELECT a FROM AuditoriaEntity a " +
            "WHERE a.fecha >= :desde " +
            "AND a.resultado = 'FALLIDO' " +
            "ORDER BY a.fecha DESC")
    List<AuditoriaEntity> findSuspiciousActivity(@Param("desde") LocalDateTime desde);

    // Limpieza
    @Modifying
    @Query("DELETE FROM AuditoriaEntity a WHERE a.fecha < :fecha")
    void deleteByFechaBefore(@Param("fecha") LocalDateTime fecha);

    // Contadores
    long countByResultado(String resultado);

    // Estadísticas
    @Query("SELECT a.accion, COUNT(a), a.resultado " +
            "FROM AuditoriaEntity a " +
            "WHERE a.fecha BETWEEN :desde AND :hasta " +
            "GROUP BY a.accion, a.resultado " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> getEstadisticas(@Param("desde") LocalDateTime desde,
                                   @Param("hasta") LocalDateTime hasta);
}
