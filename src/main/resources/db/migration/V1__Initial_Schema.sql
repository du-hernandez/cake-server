-- V1__Initial_Schema.sql
-- Ubicación: src/main/resources/db/migration/

-- Tabla de ocasiones
CREATE TABLE IF NOT EXISTS ocasion
(
    id                SERIAL PRIMARY KEY,
    nombre            VARCHAR(255) NOT NULL UNIQUE,
    estado            SMALLINT     NOT NULL DEFAULT 1,
    fecha_creado      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de tortas
CREATE TABLE IF NOT EXISTS torta
(
    id                SERIAL PRIMARY KEY,
    descripcion       VARCHAR(255) NOT NULL,
    imagen            VARCHAR(255),
    fecha_creado      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de relación torta-ocasión
CREATE TABLE IF NOT EXISTS torta_ocasion
(
    torta_id          INTEGER   NOT NULL,
    ocasion_id        INTEGER   NOT NULL,
    estado            SMALLINT  NOT NULL DEFAULT 1,
    fecha_creado      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP          DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (torta_id, ocasion_id),
    FOREIGN KEY (torta_id) REFERENCES torta (id) ON DELETE CASCADE,
    FOREIGN KEY (ocasion_id) REFERENCES ocasion (id) ON DELETE CASCADE
);

-- Tabla de imágenes
CREATE TABLE IF NOT EXISTS imagenes
(
    id                SERIAL PRIMARY KEY,
    url               VARCHAR(45) NOT NULL,
    fecha_creado      TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizado TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    fk_torta          INTEGER,
    FOREIGN KEY (fk_torta) REFERENCES torta (id) ON DELETE CASCADE
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_torta_descripcion ON torta (descripcion);
CREATE INDEX idx_torta_fecha_creado ON torta (fecha_creado);
CREATE INDEX idx_ocasion_nombre ON ocasion (nombre);
CREATE INDEX idx_ocasion_estado ON ocasion (estado);
CREATE INDEX idx_torta_ocasion_torta ON torta_ocasion (torta_id);
CREATE INDEX idx_torta_ocasion_ocasion ON torta_ocasion (ocasion_id);
CREATE INDEX idx_torta_ocasion_estado ON torta_ocasion (estado);
CREATE INDEX idx_imagenes_fk_torta ON imagenes (fk_torta);

-- Índice de texto completo para búsquedas
CREATE INDEX idx_torta_descripcion_fulltext ON torta USING gin (to_tsvector('spanish', descripcion));

-- Trigger para actualizar fecha_actualizado
CREATE OR REPLACE FUNCTION update_fecha_actualizado()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.fecha_actualizado = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_torta_fecha_actualizado
    BEFORE UPDATE
    ON torta
    FOR EACH ROW
EXECUTE FUNCTION update_fecha_actualizado();

CREATE TRIGGER update_ocasion_fecha_actualizado
    BEFORE UPDATE
    ON ocasion
    FOR EACH ROW
EXECUTE FUNCTION update_fecha_actualizado();

CREATE TRIGGER update_torta_ocasion_fecha_actualizado
    BEFORE UPDATE
    ON torta_ocasion
    FOR EACH ROW
EXECUTE FUNCTION update_fecha_actualizado();

CREATE TRIGGER update_imagenes_fecha_actualizado
    BEFORE UPDATE
    ON imagenes
    FOR EACH ROW
EXECUTE FUNCTION update_fecha_actualizado();