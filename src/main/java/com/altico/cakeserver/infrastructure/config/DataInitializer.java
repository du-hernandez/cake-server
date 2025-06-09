package com.altico.cakeserver.infrastructure.config;

import com.altico.cakeserver.applications.ports.input.*;
import com.altico.cakeserver.domain.model.*;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.entity.UsuarioEntity;
import com.altico.cakeserver.infrastructure.adapters.output.persistence.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("dev") // Solo en perfil de desarrollo
    CommandLineRunner init(TortaServicePort tortaService,
                           OcasionServicePort ocasionService,
                           ImagenServicePort imagenService,
                           PermisoServicePort permisoService,
                           RolServicePort rolService,
                           UsuarioServicePort usuarioService,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {

        return args -> {
            log.info("Inicializando datos de prueba...");

            // Crear ocasiones
            Ocasion cumpleanos = ocasionService.crear(Ocasion.crear("Cumpleaños"));
            Ocasion boda = ocasionService.crear(Ocasion.crear("Boda"));
            Ocasion aniversario = ocasionService.crear(Ocasion.crear("Aniversario"));
            Ocasion graduacion = ocasionService.crear(Ocasion.crear("Graduación"));
            Ocasion babyShower = ocasionService.crear(Ocasion.crear("Baby Shower"));
            Ocasion navidad = ocasionService.crear(Ocasion.crear("Navidad"));

            log.info("Creadas {} ocasiones", ocasionService.contarActivas());

            // Crear tortas
            Torta tortaChocolate = tortaService.crear(
                    Torta.crear("Torta de Chocolate Triple", "chocolate-triple.jpg")
            );

            Torta tortaFresa = tortaService.crear(
                    Torta.crear("Torta de Fresas con Crema", "fresas-crema.jpg")
            );

            Torta tortaVainilla = tortaService.crear(
                    Torta.crear("Torta de Vainilla Clásica", "vainilla-clasica.jpg")
            );

            Torta tortaRedVelvet = tortaService.crear(
                    Torta.crear("Red Velvet con Queso Crema", "red-velvet.jpg")
            );

            Torta tortaTresLeches = tortaService.crear(
                    Torta.crear("Tres Leches Tradicional", "tres-leches.jpg")
            );

            log.info("Creadas {} tortas", tortaService.contarTotal());

            // Asociar ocasiones a tortas
            tortaService.agregarOcasion(tortaChocolate.getId(), cumpleanos.getId());
            tortaService.agregarOcasion(tortaChocolate.getId(), aniversario.getId());

            tortaService.agregarOcasion(tortaFresa.getId(), cumpleanos.getId());
            tortaService.agregarOcasion(tortaFresa.getId(), babyShower.getId());

            tortaService.agregarOcasion(tortaVainilla.getId(), boda.getId());
            tortaService.agregarOcasion(tortaVainilla.getId(), graduacion.getId());

            tortaService.agregarOcasion(tortaRedVelvet.getId(), aniversario.getId());
            tortaService.agregarOcasion(tortaRedVelvet.getId(), navidad.getId());

            tortaService.agregarOcasion(tortaTresLeches.getId(), cumpleanos.getId());

            // Agregar imágenes adicionales
            imagenService.crear(Imagen.crear(
                    "chocolate-triple-detalle1.jpg", tortaChocolate.getId()
            ));
            imagenService.crear(Imagen.crear(
                    "chocolate-triple-detalle2.jpg", tortaChocolate.getId()
            ));

            imagenService.crear(Imagen.crear(
                    "fresas-crema-detalle1.jpg", tortaFresa.getId()
            ));

            imagenService.crear(Imagen.crear(
                    "red-velvet-detalle1.jpg", tortaRedVelvet.getId()
            ));

            log.info("Datos de prueba inicializados correctamente");

            // Mostrar estadísticas
            log.info("Estadísticas:");
            log.info("- Total de tortas: {}", tortaService.contarTotal());
            log.info("- Ocasiones activas: {}", ocasionService.contarActivas());
            log.info("- Ocasiones más populares:");
            ocasionService.obtenerOcasionesMasPopulares(3).forEach(stat -> {
                log.info("  * {}: {} tortas", stat.get("nombre"), stat.get("cantidadTortas"));
            });


            // Crear usuario
            UsuarioEntity usuarioAdmin = new UsuarioEntity(
                    "admin",
                    "admin@tortas.com",
                    "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6"
            );
            usuarioAdmin.getRoles().add("ROLE_ADMIN");
            usuarioRepository.save(usuarioAdmin);

            UsuarioEntity usuarioUser = new UsuarioEntity(
                    "user",
                    "user@tortas.com",
                    "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6"
            );
            // Asignar rol por defecto
            usuarioUser.getRoles().add("ROLE_USER");
            usuarioRepository.save(usuarioUser);

            UsuarioEntity usuarioViewer = new UsuarioEntity(
                    "viewer",
                    "viewer@tortas.com",
                    "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6"
            );
            usuarioViewer.getRoles().add("ROLE_VIEWER");
            usuarioRepository.save(usuarioViewer);
        };
    }
}