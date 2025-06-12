package com.altico.cakeserver.applications.ports.input;

import com.altico.cakeserver.domain.model.Torta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Puerto de entrada para el servicio de Tortas
 * Define las operaciones disponibles para la capa de aplicación
 */
public interface TortaServicePort {

    // Operaciones CRUD básicas
    Torta crear(Torta torta);
    Torta obtenerPorId(Integer id);
    Torta actualizar(Integer id, Torta torta);
    void eliminar(Integer id);

    // Búsquedas y listados
    List<Torta> listarTodas();
    Page<Torta> listarPaginado(Pageable pageable);
    List<Torta> buscarPorDescripcion(String descripcion);

    // Operaciones con ocasiones
    Torta agregarOcasion(Integer tortaId, Integer ocasionId);
    Torta removerOcasion(Integer tortaId, Integer ocasionId);
    List<Torta> buscarPorOcasion(Integer ocasionId);
    List<Torta> buscarPorOcasiones(List<Integer> ocasionIds);

    // Operaciones con imágenes
    Torta agregarImagen(Integer tortaId, String urlImagen);
    Torta removerImagen(Integer tortaId, Integer imagenId);
    Torta actualizarImagenPrincipal(Integer tortaId, String urlImagen);

    // Consultas especiales
    List<Torta> buscarRecientes(int cantidad);
    long contarTotal();
    boolean existePorId(Integer id);
}