package com.altico.cakeserver.domain.model;

import java.time.LocalDateTime;
import java.util.List;

// Resultado de Sincronización
public record SincronizacionResult(
        String mensaje,
        int permisosCreados,
        int permisosActualizados,
        int permisosDesactivados,
        List<String> nuevosRecursos,
        List<String> nuevasAcciones,
        LocalDateTime fechaSincronizacion
) {}
