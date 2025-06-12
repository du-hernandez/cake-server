package com.altico.cakeserver.applications.service;


import com.altico.cakeserver.applications.ports.input.TortaServicePort;
import com.altico.cakeserver.applications.ports.output.TortaPersistencePort;
import com.altico.cakeserver.applications.ports.output.OcasionPersistencePort;
import com.altico.cakeserver.applications.ports.output.ImagenPersistencePort;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.Torta;
import com.altico.cakeserver.domain.model.Ocasion;
import com.altico.cakeserver.domain.model.Imagen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TortaService implements TortaServicePort {

    private final TortaPersistencePort tortaPersistence;
    private final OcasionPersistencePort ocasionPersistence;
    private final ImagenPersistencePort imagenPersistence;

    //Could not autowire. No beans of 'TortaPersistencePort' type found.
    public TortaService(TortaPersistencePort tortaPersistence,
                        OcasionPersistencePort ocasionPersistence,
                        ImagenPersistencePort imagenPersistence) {
        this.tortaPersistence = tortaPersistence;
        this.ocasionPersistence = ocasionPersistence;
        this.imagenPersistence = imagenPersistence;
    }

    @Override
    public Torta crear(Torta torta) {
        // Validar que no exista una torta con la misma descripción
        List<Torta> existentes = tortaPersistence.findByDescripcionContaining(torta.getDescripcion());
        if (!existentes.isEmpty()) {
            throw new BusinessRuleViolationException(
                    "Ya existe una torta con descripción similar"
            );
        }

        return tortaPersistence.save(torta);
    }

    @Override
    @Transactional(readOnly = true)
    public Torta obtenerPorId(Integer id) {
        return tortaPersistence.findByIdWithRelations(id)
                .orElseThrow(() -> new TortaNotFoundException(id));
    }

    @Override
    public Torta actualizar(Integer id, Torta torta) {
        Torta existente = obtenerPorId(id);

        // Crear nueva instancia con los datos actualizados
        Torta actualizada = new Torta(
                id,
                torta.getDescripcion() != null ? torta.getDescripcion() : existente.getDescripcion(),
                torta.getImagen() != null ? torta.getImagen() : existente.getImagen(),
                existente.getFechaCreado(),
                existente.getFechaActualizado(),
                existente.getOcasiones(),
                existente.getImagenes()
        );

        return tortaPersistence.save(actualizada);
    }

    @Override
    public void eliminar(Integer id) {
        if (!tortaPersistence.existsById(id)) {
            throw new TortaNotFoundException(id);
        }

        // Eliminar imágenes asociadas
        imagenPersistence.deleteByTortaId(id);

        // Eliminar torta (las relaciones con ocasiones se eliminan en cascada)
        tortaPersistence.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> listarTodas() {
        return tortaPersistence.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Torta> listarPaginado(Pageable pageable) {
        return tortaPersistence.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> buscarPorDescripcion(String descripcion) {
        return tortaPersistence.findByDescripcionContaining(descripcion);
    }

    @Override
    public Torta agregarOcasion(Integer tortaId, Integer ocasionId) {
        Torta torta = obtenerPorId(tortaId);

        // Verificar que la ocasión existe
        Ocasion ocasion = ocasionPersistence.findById(ocasionId)
                .orElseThrow(() -> new OcasionNotFoundException(ocasionId));

        // Verificar que la ocasión esté activa
        if (!ocasion.estaActiva()) {
            throw new BusinessRuleViolationException(
                    "No se puede agregar una ocasión inactiva"
            );
        }

        // Verificar que no esté ya asociada
        if (torta.tieneOcasion(ocasionId)) {
            throw new BusinessRuleViolationException(
                    "La torta ya tiene asociada esta ocasión"
            );
        }

        // Agregar la relación
        tortaPersistence.addOcasion(tortaId, ocasionId);

        // Retornar la torta actualizada
        return obtenerPorId(tortaId);
    }

    @Override
    public Torta removerOcasion(Integer tortaId, Integer ocasionId) {
        Torta torta = obtenerPorId(tortaId);

        if (!torta.tieneOcasion(ocasionId)) {
            throw new BusinessRuleViolationException(
                    "La torta no tiene asociada esta ocasión"
            );
        }

        tortaPersistence.removeOcasion(tortaId, ocasionId);

        return obtenerPorId(tortaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> buscarPorOcasion(Integer ocasionId) {
        return tortaPersistence.findByOcasionId(ocasionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> buscarPorOcasiones(List<Integer> ocasionIds) {
        return tortaPersistence.findByOcasionIds(ocasionIds);
    }

    @Override
    public Torta agregarImagen(Integer tortaId, String urlImagen) {
        Torta torta = obtenerPorId(tortaId);

        // Validar que la URL no esté duplicada
        if (imagenPersistence.existsByUrl(urlImagen)) {
            throw new BusinessRuleViolationException(
                    "Ya existe una imagen con esta URL"
            );
        }

        // Crear y guardar la imagen
        Imagen imagen = Imagen.crear(urlImagen, tortaId);
        imagenPersistence.save(imagen);

        return obtenerPorId(tortaId);
    }

    @Override
    public Torta removerImagen(Integer tortaId, Integer imagenId) {
        // Verificar que la imagen pertenece a la torta
        if (!imagenPersistence.existsByIdAndTortaId(imagenId, tortaId)) {
            throw new BusinessRuleViolationException(
                    "La imagen no pertenece a esta torta"
            );
        }

        imagenPersistence.deleteById(imagenId);

        return obtenerPorId(tortaId);
    }

    @Override
    public Torta actualizarImagenPrincipal(Integer tortaId, String urlImagen) {
        Torta torta = obtenerPorId(tortaId);
        Torta actualizada = torta.actualizarImagen(urlImagen);

        return tortaPersistence.save(actualizada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Torta> buscarRecientes(int cantidad) {
        return tortaPersistence.findRecent(cantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarTotal() {
        return tortaPersistence.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorId(Integer id) {
        return tortaPersistence.existsById(id);
    }
}
