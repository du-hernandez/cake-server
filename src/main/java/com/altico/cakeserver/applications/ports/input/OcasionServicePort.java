package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.Ocasion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * Puerto de entrada para el servicio de Ocasiones
 */
public interface OcasionServicePort {

    // Operaciones CRUD
    Ocasion crear(Ocasion ocasion);
    Ocasion obtenerPorId(Integer id);
    Ocasion actualizar(Integer id, Ocasion ocasion);
    void eliminar(Integer id);

    // Búsquedas y listados
    List<Ocasion> listarTodas();
    Page<Ocasion> listarPaginado(Pageable pageable);
    List<Ocasion> listarActivas();
    List<Ocasion> buscarPorNombre(String nombre);

    // Operaciones de negocio
    Ocasion activar(Integer id);
    Ocasion desactivar(Integer id);

    // Consultas especiales
    List<Map<String, Object>> obtenerOcasionesMasPopulares(int cantidad);
    List<Ocasion> obtenerOcasionesSinTortas();
    boolean existePorNombre(String nombre);
    long contarActivas();
}