package com.altico.cakeserver.applications.service;

import com.altico.cakeserver.applications.ports.input.OcasionServicePort;
import com.altico.cakeserver.applications.ports.output.OcasionPersistencePort;
import com.altico.cakeserver.domain.exception.*;
import com.altico.cakeserver.domain.model.Ocasion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class OcasionService implements OcasionServicePort {

    private final OcasionPersistencePort ocasionPersistence;

    public OcasionService(OcasionPersistencePort ocasionPersistence) {
        this.ocasionPersistence = ocasionPersistence;
    }

    @Override
    public Ocasion crear(Ocasion ocasion) {
        // Validar que no exista una ocasión con el mismo nombre
        if (ocasionPersistence.existsByNombre(ocasion.getNombre())) {
            throw new DuplicateOcasionException(ocasion.getNombre());
        }

        return ocasionPersistence.save(ocasion);
    }

    @Override
    @Transactional(readOnly = true)
    public Ocasion obtenerPorId(Integer id) {
        return ocasionPersistence.findById(id)
                .orElseThrow(() -> new OcasionNotFoundException(id));
    }

    @Override
    public Ocasion actualizar(Integer id, Ocasion ocasion) {
        Ocasion existente = obtenerPorId(id);

        // Si se está cambiando el nombre, verificar que no exista
        if (!existente.getNombre().equalsIgnoreCase(ocasion.getNombre()) &&
                ocasionPersistence.existsByNombre(ocasion.getNombre())) {
            throw new DuplicateOcasionException(ocasion.getNombre());
        }

        // Crear nueva instancia con los datos actualizados
        Ocasion actualizada = existente.actualizarNombre(ocasion.getNombre());

        return ocasionPersistence.save(actualizada);
    }

    @Override
    public void eliminar(Integer id) {
        if (!ocasionPersistence.existsById(id)) {
            throw new OcasionNotFoundException(id);
        }

        ocasionPersistence.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> listarTodas() {
        return ocasionPersistence.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Ocasion> listarPaginado(Pageable pageable) {
        return ocasionPersistence.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> listarActivas() {
        return ocasionPersistence.findByEstado(true);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> buscarPorNombre(String nombre) {
        return ocasionPersistence.findByNombreContaining(nombre);
    }

    @Override
    public Ocasion activar(Integer id) {
        Ocasion ocasion = obtenerPorId(id);

        if (ocasion.estaActiva()) {
            return ocasion; // Ya está activa
        }

        Ocasion activada = ocasion.activar();
        return ocasionPersistence.save(activada);
    }

    @Override
    public Ocasion desactivar(Integer id) {
        Ocasion ocasion = obtenerPorId(id);

        if (!ocasion.estaActiva()) {
            return ocasion; // Ya está inactiva
        }

        Ocasion desactivada = ocasion.desactivar();
        return ocasionPersistence.save(desactivada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> obtenerOcasionesMasPopulares(int cantidad) {
        return ocasionPersistence.findMostPopular(cantidad);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ocasion> obtenerOcasionesSinTortas() {
        return ocasionPersistence.findWithoutTortas();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existePorNombre(String nombre) {
        return ocasionPersistence.existsByNombre(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarActivas() {
        return ocasionPersistence.countByEstado(true);
    }
}
