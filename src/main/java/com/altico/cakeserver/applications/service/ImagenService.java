package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.ImagenServicePort;
import com.altico.cakeserver.applications.ports.output.ImagenPersistencePort;
import com.altico.cakeserver.applications.ports.output.TortaPersistencePort;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.Imagen;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ImagenService implements ImagenServicePort {

    private final ImagenPersistencePort imagenPersistence;
    private final TortaPersistencePort tortaPersistence;

    public ImagenService(ImagenPersistencePort imagenPersistence,
                         TortaPersistencePort tortaPersistence) {
        this.imagenPersistence = imagenPersistence;
        this.tortaPersistence = tortaPersistence;
    }

    @Override
    public Imagen crear(Imagen imagen) {
        // Validar que la torta existe
        if (imagen.getTortaId() != null &&
                !tortaPersistence.existsById(imagen.getTortaId())) {
            throw new TortaNotFoundException(imagen.getTortaId());
        }

        // Validar que la URL no esté duplicada
        if (imagenPersistence.existsByUrl(imagen.getUrl())) {
            throw new InvalidImageException(
                    "Ya existe una imagen con la URL: " + imagen.getUrl()
            );
        }

        // Validar límite de imágenes por torta (ej: máximo 10)
        if (imagen.getTortaId() != null) {
            long cantidadActual = imagenPersistence.countByTortaId(imagen.getTortaId());
            if (cantidadActual >= 10) {
                throw new BusinessRuleViolationException(
                        "La torta ya tiene el máximo de imágenes permitidas (10)"
                );
            }
        }

        return imagenPersistence.save(imagen);
    }

    @Override
    @Transactional(readOnly = true)
    public Imagen obtenerPorId(Integer id) {
        return imagenPersistence.findById(id)
                .orElseThrow(() -> new ImagenNotFoundException(id));
    }

    @Override
    public void eliminar(Integer id) {
        if (!imagenPersistence.findById(id).isPresent()) {
            throw new ImagenNotFoundException(id);
        }

        imagenPersistence.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Imagen> listarPorTorta(Integer tortaId) {
        // Verificar que la torta existe
        if (!tortaPersistence.existsById(tortaId)) {
            throw new TortaNotFoundException(tortaId);
        }

        return imagenPersistence.findByTortaId(tortaId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Imagen> listarTodas() {
        return imagenPersistence.findAll();
    }

    @Override
    public void eliminarPorTorta(Integer tortaId) {
        imagenPersistence.deleteByTortaId(tortaId);
    }

    @Override
    public int limpiarImagenesHuerfanas() {
        // Eliminar imágenes huérfanas más antiguas de 7 días
        LocalDateTime fechaLimite = LocalDateTime.now().minusDays(7);
        return imagenPersistence.deleteOrphanImagesOlderThan(fechaLimite);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorUrl(String url) {
        return imagenPersistence.existsByUrl(url);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean perteneceATorta(Integer imagenId, Integer tortaId) {
        return imagenPersistence.existsByIdAndTortaId(imagenId, tortaId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorTorta(Integer tortaId) {
        return imagenPersistence.countByTortaId(tortaId);
    }
}
