package com.altico.cakeserver.infrastructure.adapters.output.persistence;

import com.altico.cakeserver.applications.ports.output.ImagenPersistencePort;
import com.altico.cakeserver.domain.model.Imagen;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.ImagenEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.TortaEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.mapper.ImagenPersistenceMapper;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.ImagenRepository;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.TortaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class ImagenPersistenceAdapter implements ImagenPersistencePort {

    private final ImagenRepository imagenRepository;
    private final TortaRepository tortaRepository;
    private final ImagenPersistenceMapper mapper;

    public ImagenPersistenceAdapter(ImagenRepository imagenRepository,
                                    TortaRepository tortaRepository,
                                    ImagenPersistenceMapper mapper) {
        this.imagenRepository = imagenRepository;
        this.tortaRepository = tortaRepository;
        this.mapper = mapper;
    }

    @Override
    public Imagen save(Imagen imagen) {
        ImagenEntity entity = mapper.toEntity(imagen);

        // Si tiene tortaId, asociar la torta
        if (imagen.getTortaId() != null) {
            TortaEntity torta = tortaRepository.findById(imagen.getTortaId())
                    .orElseThrow(() -> new IllegalArgumentException("Torta no encontrada"));
            entity.setTorta(torta);
        }

        ImagenEntity saved = imagenRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Imagen> findById(Integer id) {
        return imagenRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public void deleteById(Integer id) {
        imagenRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Imagen> findByTortaId(Integer tortaId) {
        return imagenRepository.findByTortaIdOrderByFechaCreadoDesc(tortaId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Imagen> findAll() {
        return imagenRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByTortaId(Integer tortaId) {
        List<ImagenEntity> imagenes = imagenRepository.findByTortaId(tortaId);
        imagenRepository.deleteAll(imagenes);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Imagen> findOrphanImages() {
        return imagenRepository.findOrphanImages().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int deleteOrphanImagesOlderThan(LocalDateTime fecha) {
        List<ImagenEntity> orphanImages = imagenRepository.findOrphanImages();
        List<ImagenEntity> toDelete = orphanImages.stream()
                .filter(img -> img.getFechaCreado().isBefore(fecha))
                .collect(Collectors.toList());

        imagenRepository.deleteAll(toDelete);
        return toDelete.size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUrl(String url) {
        return imagenRepository.existsByUrl(url);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdAndTortaId(Integer id, Integer tortaId) {
        return imagenRepository.findById(id)
                .map(imagen -> imagen.getTorta() != null &&
                        imagen.getTorta().getId().equals(tortaId))
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByTortaId(Integer tortaId) {
        return imagenRepository.countByTortaId(tortaId);
    }
}
