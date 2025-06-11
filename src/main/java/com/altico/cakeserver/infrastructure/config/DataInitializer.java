package com.altico.cakeserver.infrastructure.config;

import com.altico.cakeserver.applications.ports.input.*;
import com.altico.cakeserver.applications.ports.input.dto.*;
import com.altico.cakeserver.domain.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Inicializador de datos completo para desarrollo
 * Población de todas las tablas y relaciones del sistema
 *
 * Arquitectura: Hexagonal
 * Framework: Spring Boot 3.5.0
 *
 * @author Duverney Hernandez Mora
 */
@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    @Profile("dev") // Solo en perfil de desarrollo
    CommandLineRunner initCompleteData(
            // ============== SERVICIOS DE DOMINIO ==============
            TortaServicePort tortaService,
            OcasionServicePort ocasionService,
            ImagenServicePort imagenService,
            PermisoServicePort permisoService,
            RolServicePort rolService,
            UsuarioServicePort usuarioService,
            RefreshTokenServicePort refreshTokenService) {

        return args -> {
            log.info("🚀 INICIANDO POBLACIÓN COMPLETA DE BASE DE DATOS - DESARROLLO");
            log.info("📊 Sistema: Cake Server API v1.0.0");
            log.info("🏗️  Arquitectura: Hexagonal");
            log.info("⚡ Framework: Spring Boot 3.5.0");

            try {
                // ============== PASO 1: PERMISOS DEL SISTEMA ==============
                log.info("\n📋 PASO 1: Creando permisos del sistema...");
                crearPermisosAdicionales(permisoService);

                // ============== PASO 2: ROLES AVANZADOS ==============
                log.info("\n👥 PASO 2: Configurando roles avanzados...");
                configurarRolesAvanzados(rolService, permisoService);

                // ============== PASO 3: USUARIOS DEL SISTEMA ==============
                log.info("\n👤 PASO 3: Creando usuarios del sistema...");
                crearUsuariosCompletos(usuarioService);

                // ============== PASO 4: OCASIONES ESPECIALES ==============
                log.info("\n🎉 PASO 4: Creando ocasiones especiales...");
                var ocasiones = crearOcasionesEspeciales(ocasionService);

                // ============== PASO 5: CATÁLOGO DE TORTAS ==============
                log.info("\n🎂 PASO 5: Creando catálogo completo de tortas...");
                var tortas = crearCatalogoTortas(tortaService);

                // ============== PASO 6: RELACIONES TORTAS-OCASIONES ==============
                log.info("\n🔗 PASO 6: Estableciendo relaciones tortas-ocasiones...");
                establecerRelacionesTortasOcasiones(tortaService, tortas, ocasiones);

                // ============== PASO 7: GALERÍA DE IMÁGENES ==============
                log.info("\n📸 PASO 7: Creando galería de imágenes...");
                crearGaleriaImagenes(imagenService, tortas);

                // ============== PASO 8: TOKENS DE PRUEBA ==============
                log.info("\n🔐 PASO 8: Generando tokens de prueba...");
                generarTokensDePrueba(refreshTokenService);

                // ============== REPORTE FINAL ==============
                log.info("\n📊 REPORTE FINAL DE INICIALIZACIÓN:");
                generarReporteFinal(tortaService, ocasionService, permisoService,
                        rolService, usuarioService, refreshTokenService);

                log.info("\n✅ POBLACIÓN DE BASE DE DATOS COMPLETADA EXITOSAMENTE");
                log.info("🌟 Sistema listo para desarrollo y pruebas");

            } catch (Exception e) {
                log.error("❌ ERROR DURANTE LA INICIALIZACIÓN: {}", e.getMessage(), e);
                throw e;
            }
        };
    }

    // ============== MÉTODOS PRIVADOS ESPECIALIZADOS ==============

    /**
     * Crear permisos adicionales específicos del dominio
     */
    private void crearPermisosAdicionales(PermisoServicePort permisoService) {
        try {
            // Permisos avanzados de tortas
            crearPermisoSiNoExiste(permisoService, "Exportar Catálogo",
                    "Permite exportar el catálogo completo", "tortas", "export");
            crearPermisoSiNoExiste(permisoService, "Importar Tortas",
                    "Permite importar tortas masivamente", "tortas", "import");
            crearPermisoSiNoExiste(permisoService, "Publicar Torta",
                    "Permite publicar/despublicar tortas", "tortas", "publish");

            // Permisos de reportes
            crearPermisoSiNoExiste(permisoService, "Generar Reportes",
                    "Permite generar reportes del sistema", "reportes", "generate");
            crearPermisoSiNoExiste(permisoService, "Ver Analytics",
                    "Acceso a analytics y métricas", "analytics", "view");

            // Permisos de configuración
            crearPermisoSiNoExiste(permisoService, "Configurar Cache",
                    "Permite configurar y limpiar cache", "cache", "manage");
            crearPermisoSiNoExiste(permisoService, "Backup Sistema",
                    "Permite realizar backups", "backup", "create");

            log.info("   ✅ Permisos adicionales creados");
        } catch (Exception e) {
            log.warn("   ⚠️ Error creando permisos adicionales: {}", e.getMessage());
        }
    }

    /**
     * Configurar roles avanzados con permisos específicos
     */
    private void configurarRolesAvanzados(RolServicePort rolService, PermisoServicePort permisoService) {
        try {
            // Crear roles especializados
            crearRolSiNoExiste(rolService, "ROLE_BAKER",
                    "Pastelero - Gestión completa de tortas", 75);
            crearRolSiNoExiste(rolService, "ROLE_DESIGNER",
                    "Diseñador - Gestión de imágenes y galería", 80);
            crearRolSiNoExiste(rolService, "ROLE_SALES",
                    "Ventas - Acceso a catálogos y ocasiones", 90);
            crearRolSiNoExiste(rolService, "ROLE_ANALYST",
                    "Analista - Acceso a reportes y estadísticas", 85);

            // ROLES DEFINIDOS POR DEFECTO PARA EL SISTEMA
            crearRolSiNoExiste(rolService, "ROLE_SUPER_ADMIN", "Super Administrador con todos los permisos", 1);
            crearRolSiNoExiste(rolService, "ROLE_ADMIN", "Administrador del sistema", 10);
            crearRolSiNoExiste(rolService, "ROLE_MANAGER", "Gerente con permisos de gestión", 50);
            crearRolSiNoExiste(rolService, "ROLE_USER", "Usuario regular con permisos básicos", 100);
            crearRolSiNoExiste(rolService, "ROLE_VIEWER", "Solo lectura", 500);

            // Asignar permisos específicos a roles (ejemplo para BAKER)
            try {
                var rolBaker = rolService.obtenerPorNombre("ROLE_BAKER");
                var permisoCrearTorta = permisoService.obtenerPorRecursoYAccion("tortas", "create");
                rolService.asignarPermiso(rolBaker.getId(), permisoCrearTorta.getId());
                log.info("   ✅ Permisos asignados a roles especializados");
            } catch (Exception e) {
                log.debug("   ℹ️ Permisos ya asignados o rol no encontrado");
            }

        } catch (Exception e) {
            log.warn("   ⚠️ Error configurando roles avanzados: {}", e.getMessage());
        }
    }

    /**
     * Crear usuarios completos con diferentes roles
     */
    private void crearUsuariosCompletos(UsuarioServicePort usuarioService) {
        // Usuario Super Admin
        crearUsuarioSiNoExiste(usuarioService,
                "superadmin", "superadmin@cakeserver.com", "SuperAdmin123!",
                Set.of("ROLE_SUPER_ADMIN"), true);

        // Usuarios especializados
        crearUsuarioSiNoExiste(usuarioService,
                "baker.maria", "maria@cakeserver.com", "Baker123!",
                Set.of("ROLE_BAKER", "ROLE_USER"), true);

        crearUsuarioSiNoExiste(usuarioService,
                "designer.carlos", "carlos@cakeserver.com", "Design123!",
                Set.of("ROLE_DESIGNER", "ROLE_USER"), true);

        crearUsuarioSiNoExiste(usuarioService,
                "sales.ana", "ana@cakeserver.com", "Sales123!",
                Set.of("ROLE_SALES", "ROLE_USER"), true);

        crearUsuarioSiNoExiste(usuarioService,
                "analyst.luis", "luis@cakeserver.com", "Analyst123!",
                Set.of("ROLE_ANALYST", "ROLE_USER"), true);

        // Usuario de pruebas
        crearUsuarioSiNoExiste(usuarioService,
                "testuser", "test@cakeserver.com", "Test123!",
                Set.of("ROLE_USER"), true);

        // Usuario inactivo para pruebas
        crearUsuarioSiNoExiste(usuarioService,
                "inactive.user", "inactive@cakeserver.com", "Inactive123!",
                Set.of("ROLE_USER"), false);

        log.info("   ✅ {} usuarios especializados creados", 7);
    }

    /**
     * Crear ocasiones especiales del negocio
     */
    private List<Ocasion> crearOcasionesEspeciales(OcasionServicePort ocasionService) {
        var ocasiones = List.of(
                // Ocasiones principales
                crearOcasionSiNoExiste(ocasionService, "Cumpleaños"),
                crearOcasionSiNoExiste(ocasionService, "Boda"),
                crearOcasionSiNoExiste(ocasionService, "Aniversario"),
                crearOcasionSiNoExiste(ocasionService, "Graduación"),
                crearOcasionSiNoExiste(ocasionService, "Baby Shower"),
                crearOcasionSiNoExiste(ocasionService, "Quinceañera"),

                // Ocasiones estacionales
                crearOcasionSiNoExiste(ocasionService, "Navidad"),
                crearOcasionSiNoExiste(ocasionService, "Año Nuevo"),
                crearOcasionSiNoExiste(ocasionService, "San Valentín"),
                crearOcasionSiNoExiste(ocasionService, "Día de la Madre"),
                crearOcasionSiNoExiste(ocasionService, "Día del Padre"),
                crearOcasionSiNoExiste(ocasionService, "Halloween"),

                // Ocasiones especiales
                crearOcasionSiNoExiste(ocasionService, "Bautizo"),
                crearOcasionSiNoExiste(ocasionService, "Primera Comunión"),
                crearOcasionSiNoExiste(ocasionService, "Confirmación"),
                crearOcasionSiNoExiste(ocasionService, "Despedida de Soltera"),
                crearOcasionSiNoExiste(ocasionService, "Jubilación"),
                crearOcasionSiNoExiste(ocasionService, "Inauguración"),
                crearOcasionSiNoExiste(ocasionService, "Reunión Familiar"),
                crearOcasionSiNoExiste(ocasionService, "Celebración Empresarial")
        );

        log.info("   ✅ {} ocasiones especiales creadas", ocasiones.size());
        return ocasiones;
    }

    /**
     * Crear catálogo completo de tortas
     */
    private List<Torta> crearCatalogoTortas(TortaServicePort tortaService) {
        var tortas = List.of(
                // Tortas clásicas
                crearTortaSiNoExiste(tortaService,
                        "Torta de Chocolate Triple", "https://images.cakeserver.com/chocolate-triple.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Red Velvet con Queso Crema", "https://images.cakeserver.com/red-velvet.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Tres Leches Tradicional", "https://images.cakeserver.com/tres-leches.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Vainilla Clásica", "https://images.cakeserver.com/vainilla-clasica.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Cheesecake de Frutos Rojos", "https://images.cakeserver.com/cheesecake.jpg"),

                // Tortas de frutas
                crearTortaSiNoExiste(tortaService,
                        "Torta de Fresas con Crema", "https://images.cakeserver.com/fresas-crema.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Mango y Maracuyá", "https://images.cakeserver.com/mango-maracuya.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Piña Colada", "https://images.cakeserver.com/pina-colada.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Limón", "https://images.cakeserver.com/limon.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Durazno", "https://images.cakeserver.com/durazno.jpg"),

                // Tortas especiales
                crearTortaSiNoExiste(tortaService,
                        "Torta Selva Negra", "https://images.cakeserver.com/selva-negra.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Zanahoria", "https://images.cakeserver.com/zanahoria.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Café Moka", "https://images.cakeserver.com/cafe-moka.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Coco", "https://images.cakeserver.com/coco.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Nutella", "https://images.cakeserver.com/nutella.jpg"),

                // Tortas infantiles
                crearTortaSiNoExiste(tortaService,
                        "Torta de Unicornio", "https://images.cakeserver.com/unicornio.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Superhéroes", "https://images.cakeserver.com/superheroes.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Princesas", "https://images.cakeserver.com/princesas.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Dinosaurios", "https://images.cakeserver.com/dinosaurios.jpg"),
                crearTortaSiNoExiste(tortaService,
                        "Torta de Minecraft", "https://images.cakeserver.com/minecraft.jpg")
        );

        log.info("   ✅ {} tortas creadas en el catálogo", tortas.size());
        return tortas;
    }

    /**
     * Establecer relaciones inteligentes entre tortas y ocasiones
     */
    private void establecerRelacionesTortasOcasiones(TortaServicePort tortaService,
                                                     List<Torta> tortas,
                                                     List<Ocasion> ocasiones) {

        // Mapeo inteligente basado en el dominio del negocio
        var relaciones = Map.of(
                "Cumpleaños", List.of("Chocolate Triple", "Vainilla Clásica", "Fresas", "Unicornio", "Superhéroes", "Princesas"),
                "Boda", List.of("Red Velvet", "Tres Leches", "Vainilla Clásica", "Fresas"),
                "Aniversario", List.of("Red Velvet", "Chocolate Triple", "Selva Negra"),
                "Baby Shower", List.of("Fresas", "Vainilla Clásica", "Limón", "Coco"),
                "Quinceañera", List.of("Red Velvet", "Tres Leches", "Princesas", "Fresas"),
                "Navidad", List.of("Chocolate Triple", "Selva Negra", "Tres Leches"),
                "San Valentín", List.of("Red Velvet", "Chocolate Triple", "Fresas"),
                "Graduación", List.of("Tres Leches", "Zanahoria", "Café Moka")
        );

        int relacionesCreadas = 0;
        for (var entrada : relaciones.entrySet()) {
            var nombreOcasion = entrada.getKey();
            var tiposTorta = entrada.getValue();

            var ocasion = ocasiones.stream()
                    .filter(o -> o.getNombre().contains(nombreOcasion))
                    .findFirst();

            if (ocasion.isPresent()) {
                for (String tipoTorta : tiposTorta) {
                    var torta = tortas.stream()
                            .filter(t -> t.getDescripcion().contains(tipoTorta))
                            .findFirst();

                    if (torta.isPresent()) {
                        try {
                            tortaService.agregarOcasion(torta.get().getId(), ocasion.get().getId());
                            relacionesCreadas++;
                        } catch (Exception e) {
                            log.debug("   ℹ️ Relación ya existe: {} - {}",
                                    tipoTorta, nombreOcasion);
                        }
                    }
                }
            }
        }

        log.info("   ✅ {} relaciones torta-ocasión establecidas", relacionesCreadas);
    }

    /**
     * Crear galería completa de imágenes
     */
    private void crearGaleriaImagenes(ImagenServicePort imagenService, List<Torta> tortas) {
        int imagenesCreadas = 0;

        for (Torta torta : tortas) {
            // Crear 2-4 imágenes adicionales por torta
            String baseName = torta.getDescripcion().toLowerCase()
                    .replaceAll("[^a-z0-9]", "-")
                    .replaceAll("-+", "-");

            var imagenes = List.of(
                    "https://images.cakeserver.com/" + baseName + "-detalle1.jpg",
                    "https://images.cakeserver.com/" + baseName + "-detalle2.jpg",
                    "https://images.cakeserver.com/" + baseName + "-proceso.jpg",
                    "https://images.cakeserver.com/" + baseName + "-decoracion.jpg"
            );

            for (String url : imagenes) {
                try {
                    if (!imagenService.existePorUrl(url)) {
                        imagenService.crear(Imagen.crear(url, torta.getId()));
                        imagenesCreadas++;
                    }
                } catch (Exception e) {
                    log.debug("   ℹ️ Imagen ya existe o error: {}", url);
                }
            }
        }

        log.info("   ✅ {} imágenes adicionales creadas en la galería", imagenesCreadas);
    }

    /**
     * Generar tokens de prueba para diferentes usuarios
     */
    private void generarTokensDePrueba(RefreshTokenServicePort refreshTokenService) {
        try {
            // Simulamos tokens para diferentes dispositivos/usuarios
            var dispositivos = List.of(
                    new DispositivoSimulado("admin", "Desktop - Chrome", "192.168.1.100", "Chrome/120.0.0.0"),
                    new DispositivoSimulado("baker.maria", "Mobile - Safari", "192.168.1.101", "Safari/17.0"),
                    new DispositivoSimulado("designer.carlos", "Desktop - Firefox", "192.168.1.102", "Firefox/121.0"),
                    new DispositivoSimulado("sales.ana", "Tablet - Edge", "192.168.1.103", "Edge/120.0.0.0"),
                    new DispositivoSimulado("testuser", "Mobile - Chrome", "192.168.1.104", "Chrome Mobile/120.0")
            );

            int tokensCreados = 0;
            for (var dispositivo : dispositivos) {
                try {
                    refreshTokenService.crearToken(
                            dispositivo.username(),
                            dispositivo.deviceInfo(),
                            dispositivo.ipAddress(),
                            dispositivo.userAgent()
                    );
                    tokensCreados++;
                } catch (Exception e) {
                    log.debug("   ℹ️ Error creando token para {}: {}",
                            dispositivo.username(), e.getMessage());
                }
            }

            log.info("   ✅ {} tokens de prueba generados", tokensCreados);
        } catch (Exception e) {
            log.warn("   ⚠️ Error generando tokens de prueba: {}", e.getMessage());
        }
    }

    /**
     * Generar reporte final de inicialización
     */
    private void generarReporteFinal(TortaServicePort tortaService,
                                     OcasionServicePort ocasionService,
                                     PermisoServicePort permisoService,
                                     RolServicePort rolService,
                                     UsuarioServicePort usuarioService,
                                     RefreshTokenServicePort refreshTokenService) {

        log.info("   📊 Total de tortas: {}", tortaService.contarTotal());
        log.info("   🎉 Ocasiones activas: {}", ocasionService.contarActivas());
        log.info("   👥 Usuarios totales: {}", usuarioService.obtenerEstadisticas().totalUsuarios());
        log.info("   🔐 Roles configurados: {}", rolService.obtenerEstadisticasRoles().get("total"));
        log.info("   📋 Permisos del sistema: {}", permisoService.obtenerEstadisticasPermisos().get("total"));

        // Estadísticas de tokens
        var tokenStats = refreshTokenService.obtenerEstadisticas();
        log.info("   📊 Total Tokens: {}", tokenStats.totalTokens());
        log.info("   🟢 Tokens Activos: {}", tokenStats.tokensActivos());
        log.info("   🔴 Tokens Expirados: {}", tokenStats.tokensExpirados());
        log.info("   🚫 Tokens Revocados: {}", tokenStats.tokensRevocados());
        log.info("   ⏳ Tokens Por Expirar en 24h: {}", tokenStats.tokensPorExpirar24h());
        log.info("   👤 Sesiones Únicas: {}", tokenStats.sesionesUnicas());
        log.info("   📱 Dispositivos Únicos: {}", tokenStats.dispositivosUnicos());
        log.info("   📈 Promedio sesiones por Usuario: {}", tokenStats.promedioSesionesPorUsuario());


        // Top ocasiones más populares
        List<Map<String, Object>> ocasionesPopulares = ocasionService.obtenerOcasionesMasPopulares(3);
        log.info("   🏆 TOP 3 OCASIONES MÁS POPULARES:");
        ocasionesPopulares.forEach(stat -> {
            log.info("      • {}: {} tortas asociadas",
                    stat.get("nombre"), stat.get("cantidadTortas"));
        });

        // Usuarios por rol
        var usuarioStats = usuarioService.obtenerEstadisticas();
        log.info("   👤 DISTRIBUCIÓN DE USUARIOS POR ROL:");
        log.info("      • Usuarios totales: {}", usuarioStats.totalUsuarios());
        log.info("      • Usuarios activos: {}", usuarioStats.usuariosActivos());
        log.info("      • Usuarios inactivos: {}", usuarioStats.usuariosInactivos());
        log.info("      • Sin roles asignados: {}", usuarioStats.usuariosSinRoles());
    }

    // ============== MÉTODOS AUXILIARES ==============

    private void crearPermisoSiNoExiste(PermisoServicePort service, String nombre,
                                        String descripcion, String recurso, String accion) {
        try {
            if (!service.existePermiso(recurso, accion)) {
                service.crearPermiso(new CreatePermisoCommand(nombre, descripcion, recurso, accion));
            }
        } catch (Exception e) {
            log.debug("Permiso ya existe: {}", nombre);
        }
    }

    private void crearRolSiNoExiste(RolServicePort service, String nombre,
                                    String descripcion, int prioridad) {
        try {
            if (!service.existeRol(nombre)) {
                service.crearRol(new CreateRolCommand(nombre, descripcion, prioridad, Set.of()));
            }
        } catch (Exception e) {
            log.debug("Rol ya existe: {}", nombre);
        }
    }

    private void crearUsuarioSiNoExiste(UsuarioServicePort service, String username,
                                        String email, String password, Set<String> roles, boolean activo) {
        try {
            if (!service.existeUsername(username)) {
                log.info("No existe el Usuario: {}. A continuación se creará", username);
                service.crearUsuario(new CreateUsuarioCommand(username, email, password, activo, roles));
                UsuarioCompleto usuarioCompleto = service.obtenerPorUsername(username);
                log.info("Usuario completo creado: {}", usuarioCompleto);
            }
        } catch (Exception e) {
            log.debug("Usuario ya existe: {}", username);
        }
    }

    private Ocasion crearOcasionSiNoExiste(OcasionServicePort service, String nombre) {
        try {
            if (!service.existePorNombre(nombre)) {
                return service.crear(Ocasion.crear(nombre));
            } else {
                return service.buscarPorNombre(nombre).getFirst();
            }
        } catch (Exception e) {
            log.debug("Ocasión ya existe: {}", nombre);
            return service.buscarPorNombre(nombre).getFirst();
        }
    }

    private Torta crearTortaSiNoExiste(TortaServicePort service, String descripcion, String imagen) {
        try {
            var existentes = service.buscarPorDescripcion(descripcion);
            if (existentes.isEmpty()) {
                return service.crear(Torta.crear(descripcion, imagen));
            } else {
                return existentes.getFirst();
            }
        } catch (Exception e) {
            log.debug("Torta ya existe: {}", descripcion);
            return service.buscarPorDescripcion(descripcion).getFirst();
        }
    }

    // Record auxiliar para simulación de dispositivos
    private record DispositivoSimulado(String username, String deviceInfo,
                                       String ipAddress, String userAgent) {}
}