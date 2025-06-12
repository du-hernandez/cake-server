package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.Imagen;
import java.util.List;

/**
 * Puerto de entrada para el servicio de Imágenes
 */
public interface ImagenServicePort {

    // Operaciones CRUD
    Imagen crear(Imagen imagen);
    Imagen obtenerPorId(Integer id);
    void eliminar(Integer id);

    // Búsquedas
    List<Imagen> listarPorTorta(Integer tortaId);
    List<Imagen> listarTodas();

    // Operaciones de negocio
    void eliminarPorTorta(Integer tortaId);
    int limpiarImagenesHuerfanas();

    // Validaciones
    boolean existePorUrl(String url);
    boolean perteneceATorta(Integer imagenId, Integer tortaId);
    long contarPorTorta(Integer tortaId);
}
