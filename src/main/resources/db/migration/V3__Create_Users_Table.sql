-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios
(
    id
    BIGSERIAL
    PRIMARY
    KEY,
    username
    VARCHAR
(
    50
) NOT NULL UNIQUE,
    email VARCHAR
(
    100
) NOT NULL UNIQUE,
    password VARCHAR
(
    255
) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- Tabla de roles de usuario
CREATE TABLE IF NOT EXISTS usuario_roles
(
    usuario_id
    BIGINT
    NOT
    NULL,
    rol
    VARCHAR
(
    50
) NOT NULL,
    PRIMARY KEY
(
    usuario_id,
    rol
),
    FOREIGN KEY
(
    usuario_id
) REFERENCES usuarios
(
    id
) ON DELETE CASCADE
    );

-- Índices
CREATE INDEX idx_usuario_username ON usuarios (username);
CREATE INDEX idx_usuario_email ON usuarios (email);
CREATE INDEX idx_usuario_activo ON usuarios (activo);

-- Trigger para actualizar fecha_actualizado
CREATE TRIGGER update_usuarios_fecha_actualizado
    BEFORE UPDATE
    ON usuarios
    FOR EACH ROW
    EXECUTE FUNCTION update_fecha_actualizado();

-- Insertar usuarios de prueba (contraseña: password123)
INSERT INTO usuarios (username, email, password, activo)
VALUES ('admin', 'admin@tortas.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', true),
       ('user', 'user@tortas.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', true),
       ('viewer', 'viewer@tortas.com', '$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6', true);

-- Asignar roles
INSERT INTO usuario_roles (usuario_id, rol)
VALUES ((SELECT id FROM usuarios WHERE username = 'admin'), 'ROLE_ADMIN'),
       ((SELECT id FROM usuarios WHERE username = 'admin'), 'ROLE_USER'),
       ((SELECT id FROM usuarios WHERE username = 'user'), 'ROLE_USER'),
       ((SELECT id FROM usuarios WHERE username = 'viewer'), 'ROLE_VIEWER');