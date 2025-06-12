-- V4__Create_Permisos_And_Roles_Tables.sql
-- Ubicación: src/main/resources/db/migration/

-- Tabla de permisos
CREATE TABLE IF NOT EXISTS permisos
(
    id                SERIAL PRIMARY KEY,
    nombre            VARCHAR(100) NOT NULL UNIQUE,
    descripcion       VARCHAR(255),
    recurso           VARCHAR(50)  NOT NULL,
    accion            VARCHAR(50)  NOT NULL,
    activo            BOOLEAN      NOT NULL DEFAULT true,
    fecha_creado      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_permiso_nombre UNIQUE (nombre),
    CONSTRAINT uk_permiso_recurso_accion UNIQUE (recurso, accion)
    );

-- Tabla de roles completos
CREATE TABLE IF NOT EXISTS roles
(
    id                SERIAL PRIMARY KEY,
    nombre            VARCHAR(50)  NOT NULL UNIQUE,
    descripcion       VARCHAR(255),
    prioridad         INTEGER      NOT NULL DEFAULT 999,
    activo            BOOLEAN      NOT NULL DEFAULT true,
    fecha_creado      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_rol_nombre UNIQUE (nombre)
    );

-- Tabla de relación roles-permisos
CREATE TABLE IF NOT EXISTS rol_permisos
(
    rol_id     INTEGER NOT NULL,
    permiso_id INTEGER NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES roles (id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES permisos (id) ON DELETE CASCADE
    );

-- Tabla de auditoría de usuarios
CREATE TABLE IF NOT EXISTS auditoria_usuarios
(
    id          BIGSERIAL PRIMARY KEY,
    usuario_id  BIGINT,
    accion      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500),
    ip_address  VARCHAR(45),
    user_agent  VARCHAR(500),
    fecha       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    resultado   VARCHAR(50),
    FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE SET NULL
    );

-- Tabla de refresh tokens
CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id               VARCHAR(255) PRIMARY KEY,
    username         VARCHAR(50)  NOT NULL,
    device_info      VARCHAR(255),
    ip_address       VARCHAR(45),
    user_agent       VARCHAR(500),
    activo           BOOLEAN      NOT NULL DEFAULT true,
    fecha_creacion   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP    NOT NULL,
    ultimo_uso       TIMESTAMP,
    FOREIGN KEY (username) REFERENCES usuarios (username) ON DELETE CASCADE
    );

-- Índices para permisos
CREATE INDEX idx_permiso_nombre ON permisos (nombre);
CREATE INDEX idx_permiso_recurso ON permisos (recurso);
CREATE INDEX idx_permiso_accion ON permisos (accion);
CREATE INDEX idx_permiso_activo ON permisos (activo);
CREATE INDEX idx_permiso_recurso_accion ON permisos (recurso, accion);

-- Índices para roles
CREATE INDEX idx_rol_nombre ON roles (nombre);
CREATE INDEX idx_rol_prioridad ON roles (prioridad);
CREATE INDEX idx_rol_activo ON roles (activo);

-- Índices para rol_permisos
CREATE INDEX idx_rol_permisos_rol ON rol_permisos (rol_id);
CREATE INDEX idx_rol_permisos_permiso ON rol_permisos (permiso_id);

-- Índices para auditoría
CREATE INDEX idx_auditoria_usuario ON auditoria_usuarios (usuario_id);
CREATE INDEX idx_auditoria_fecha ON auditoria_usuarios (fecha);
CREATE INDEX idx_auditoria_accion ON auditoria_usuarios (accion);

-- Índices para refresh tokens
CREATE INDEX idx_refresh_token_username ON refresh_tokens (username);
CREATE INDEX idx_refresh_token_device ON refresh_tokens (device_info);
CREATE INDEX idx_refresh_token_expiracion ON refresh_tokens (fecha_expiracion);
CREATE INDEX idx_refresh_token_activo ON refresh_tokens (activo);

-- Trigger para actualizar fecha_actualizado en permisos
CREATE TRIGGER update_permisos_fecha_actualizado
    BEFORE UPDATE
    ON permisos
    FOR EACH ROW
    EXECUTE FUNCTION update_fecha_actualizado();

-- Trigger para actualizar fecha_actualizado en roles
CREATE TRIGGER update_roles_fecha_actualizado
    BEFORE UPDATE
    ON roles
    FOR EACH ROW
    EXECUTE FUNCTION update_fecha_actualizado();

-- Insertar permisos básicos del sistema
INSERT INTO permisos (nombre, descripcion, recurso, accion) VALUES
-- Permisos para tortas
('Crear Torta', 'Permite crear nuevas tortas', 'tortas', 'create'),
('Leer Torta', 'Permite ver tortas', 'tortas', 'read'),
('Actualizar Torta', 'Permite actualizar tortas existentes', 'tortas', 'update'),
('Eliminar Torta', 'Permite eliminar tortas', 'tortas', 'delete'),

-- Permisos para ocasiones
('Crear Ocasión', 'Permite crear nuevas ocasiones', 'ocasiones', 'create'),
('Leer Ocasión', 'Permite ver ocasiones', 'ocasiones', 'read'),
('Actualizar Ocasión', 'Permite actualizar ocasiones', 'ocasiones', 'update'),
('Eliminar Ocasión', 'Permite eliminar ocasiones', 'ocasiones', 'delete'),

-- Permisos para imágenes
('Crear Imagen', 'Permite subir nuevas imágenes', 'imagenes', 'create'),
('Leer Imagen', 'Permite ver imágenes', 'imagenes', 'read'),
('Eliminar Imagen', 'Permite eliminar imágenes', 'imagenes', 'delete'),

-- Permisos administrativos
('Gestionar Usuarios', 'Administración completa de usuarios', 'usuarios', 'manage'),
('Gestionar Roles', 'Administración completa de roles', 'roles', 'manage'),
('Gestionar Permisos', 'Administración completa de permisos', 'permisos', 'manage'),
('Gestionar Tokens', 'Administración de tokens y sesiones', 'tokens', 'manage'),
('Ver Auditoría', 'Acceso a logs de auditoría', 'auditoria', 'read'),

-- Permisos del sistema
('Acceder Dashboard', 'Acceso al panel de administración', 'dashboard', 'access'),
('Ver Estadísticas', 'Acceso a estadísticas del sistema', 'estadisticas', 'read'),
('Configurar Sistema', 'Configuración del sistema', 'sistema', 'configure');

-- Insertar roles básicos
INSERT INTO roles (nombre, descripcion, prioridad) VALUES
                                                       ('ROLE_SUPER_ADMIN', 'Super Administrador con todos los permisos', 1),
                                                       ('ROLE_ADMIN', 'Administrador del sistema', 10),
                                                       ('ROLE_MANAGER', 'Gerente con permisos de gestión', 50),
                                                       ('ROLE_USER', 'Usuario regular con permisos básicos', 100),
                                                       ('ROLE_VIEWER', 'Solo lectura', 500);

-- Asignar todos los permisos al SUPER_ADMIN
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT
    (SELECT id FROM roles WHERE nombre = 'ROLE_SUPER_ADMIN'),
    p.id
FROM permisos p;

-- Asignar permisos básicos al ADMIN (todos excepto configuración del sistema)
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT
    (SELECT id FROM roles WHERE nombre = 'ROLE_ADMIN'),
    p.id
FROM permisos p
WHERE p.recurso != 'sistema' OR p.accion != 'configure';

-- Asignar permisos de gestión al MANAGER
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT
    (SELECT id FROM roles WHERE nombre = 'ROLE_MANAGER'),
    p.id
FROM permisos p
WHERE p.recurso IN ('tortas', 'ocasiones', 'imagenes')
   OR (p.recurso = 'dashboard' AND p.accion = 'access')
   OR (p.recurso = 'estadisticas' AND p.accion = 'read');

-- Asignar permisos básicos al USER
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT
    (SELECT id FROM roles WHERE nombre = 'ROLE_USER'),
    p.id
FROM permisos p
WHERE (p.recurso IN ('tortas', 'ocasiones', 'imagenes') AND p.accion IN ('create', 'read', 'update'))
   OR (p.recurso = 'dashboard' AND p.accion = 'access');

-- Asignar solo permisos de lectura al VIEWER
INSERT INTO rol_permisos (rol_id, permiso_id)
SELECT
    (SELECT id FROM roles WHERE nombre = 'ROLE_VIEWER'),
    p.id
FROM permisos p
WHERE p.accion = 'read'
   OR (p.recurso = 'dashboard' AND p.accion = 'access');

-- Actualizar usuarios existentes para usar los nuevos roles
-- Nota: Esto actualiza la tabla usuario_roles que ya existe
UPDATE usuario_roles
SET rol = 'ROLE_SUPER_ADMIN'
WHERE rol = 'ROLE_ADMIN'
  AND usuario_id = (SELECT id FROM usuarios WHERE username = 'admin');

-- Agregar comentarios a las tablas
COMMENT ON TABLE permisos IS 'Permisos granulares del sistema basados en recurso:acción';
COMMENT ON TABLE roles IS 'Roles jerárquicos con conjuntos de permisos';
COMMENT ON TABLE rol_permisos IS 'Relación many-to-many entre roles y permisos';
COMMENT ON TABLE auditoria_usuarios IS 'Log de auditoría de acciones de usuarios';
COMMENT ON TABLE refresh_tokens IS 'Tokens de refresco para gestión de sesiones';

-- Comentarios en columnas importantes
COMMENT ON COLUMN permisos.recurso IS 'Recurso del sistema (ej: tortas, usuarios, roles)';
COMMENT ON COLUMN permisos.accion IS 'Acción permitida (ej: create, read, update, delete, manage)';
COMMENT ON COLUMN roles.prioridad IS 'Prioridad del rol (menor número = mayor prioridad)';
COMMENT ON COLUMN refresh_tokens.device_info IS 'Información del dispositivo para identificación';

-- Estadísticas iniciales (opcional, para verificar la inserción)
-- SELECT
--     'Permisos creados' as tipo,
--     COUNT(*) as cantidad
-- FROM permisos
-- UNION ALL
-- SELECT
--     'Roles creados' as tipo,
--     COUNT(*) as cantidad
-- FROM roles
-- UNION ALL
-- SELECT
--     'Asignaciones rol-permiso' as tipo,
--     COUNT(*) as cantidad
-- FROM rol_permisos;