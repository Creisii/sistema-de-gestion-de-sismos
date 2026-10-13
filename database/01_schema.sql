-- =====================================================================
-- Sistema de Gestión de Sismos del Perú (CSSP) - Esquema de base de datos
-- MySQL 8 o superior.
--
-- ATENCIÓN: este script BORRA y vuelve a crear todas las tablas.
-- Ejecútalo la primera vez y cada vez que quieras volver a los datos de ejemplo.
-- Después ejecuta 02_datos_iniciales.sql.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS cssp_sismos
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE cssp_sismos;

DROP TABLE IF EXISTS seguimiento_sismo;
DROP TABLE IF EXISTS reporte_afectacion;
DROP TABLE IF EXISTS sismo;
DROP TABLE IF EXISTS estacion_monitoreo;
DROP TABLE IF EXISTS distrito;
DROP TABLE IF EXISTS provincia;
DROP TABLE IF EXISTS departamento;

-- ---------------------------------------------------------------------
-- Ubicaciones: Departamento 1 ── N Provincia 1 ── N Distrito
-- Los códigos son los de ubigeo del INEI (2, 4 y 6 dígitos).
-- ---------------------------------------------------------------------
CREATE TABLE departamento (
    id      BIGINT       PRIMARY KEY AUTO_INCREMENT,
    codigo  CHAR(2)      NOT NULL,
    nombre  VARCHAR(60)  NOT NULL,
    CONSTRAINT uq_departamento_codigo UNIQUE (codigo),
    CONSTRAINT uq_departamento_nombre UNIQUE (nombre)
);

CREATE TABLE provincia (
    id              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    codigo          CHAR(4)      NOT NULL,
    nombre          VARCHAR(80)  NOT NULL,
    departamento_id BIGINT       NOT NULL,
    CONSTRAINT uq_provincia_codigo UNIQUE (codigo),
    CONSTRAINT uq_provincia_nombre UNIQUE (departamento_id, nombre),
    CONSTRAINT fk_provincia_departamento FOREIGN KEY (departamento_id) REFERENCES departamento (id)
);

CREATE TABLE distrito (
    id           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    codigo       CHAR(6)      NOT NULL,
    nombre       VARCHAR(80)  NOT NULL,
    provincia_id BIGINT       NOT NULL,
    CONSTRAINT uq_distrito_codigo UNIQUE (codigo),
    CONSTRAINT uq_distrito_nombre UNIQUE (provincia_id, nombre),
    CONSTRAINT fk_distrito_provincia FOREIGN KEY (provincia_id) REFERENCES provincia (id)
);

-- ---------------------------------------------------------------------
-- Estaciones de monitoreo: Distrito 1 ── N Estación
-- ---------------------------------------------------------------------
CREATE TABLE estacion_monitoreo (
    id                BIGINT       PRIMARY KEY AUTO_INCREMENT,
    codigo            VARCHAR(10)  NOT NULL,
    nombre            VARCHAR(100) NOT NULL,
    distrito_id       BIGINT       NOT NULL,
    latitud           DOUBLE       NOT NULL,
    longitud          DOUBLE       NOT NULL,
    fecha_instalacion DATE         NOT NULL,
    estado            VARCHAR(10)  NOT NULL DEFAULT 'Activa',
    CONSTRAINT uq_estacion_codigo UNIQUE (codigo),
    CONSTRAINT fk_estacion_distrito FOREIGN KEY (distrito_id) REFERENCES distrito (id),
    CONSTRAINT chk_estacion_estado CHECK (estado IN ('Activa', 'Inactiva')),
    CONSTRAINT chk_estacion_latitud CHECK (latitud BETWEEN -90 AND 90),
    CONSTRAINT chk_estacion_longitud CHECK (longitud BETWEEN -180 AND 180)
);

-- ---------------------------------------------------------------------
-- Sismos: Distrito 1 ── N Sismo, Estación 1 ── N Sismo (estación opcional)
-- ---------------------------------------------------------------------
CREATE TABLE sismo (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    codigo      VARCHAR(20)  NOT NULL,
    fecha_hora  DATETIME     NOT NULL,
    magnitud    DOUBLE       NOT NULL,
    profundidad DOUBLE       NOT NULL,
    latitud     DOUBLE       NOT NULL,
    longitud    DOUBLE       NOT NULL,
    distrito_id BIGINT       NOT NULL,
    referencia  VARCHAR(150) NULL,
    intensidad  VARCHAR(10)  NULL,
    estado      VARCHAR(20)  NOT NULL DEFAULT 'Registrado',
    estacion_id BIGINT       NULL,
    CONSTRAINT uq_sismo_codigo UNIQUE (codigo),
    CONSTRAINT fk_sismo_distrito FOREIGN KEY (distrito_id) REFERENCES distrito (id),
    CONSTRAINT fk_sismo_estacion FOREIGN KEY (estacion_id) REFERENCES estacion_monitoreo (id),
    CONSTRAINT chk_sismo_magnitud CHECK (magnitud > 0),
    CONSTRAINT chk_sismo_profundidad CHECK (profundidad >= 0),
    CONSTRAINT chk_sismo_latitud CHECK (latitud BETWEEN -90 AND 90),
    CONSTRAINT chk_sismo_longitud CHECK (longitud BETWEEN -180 AND 180),
    CONSTRAINT chk_sismo_estado CHECK (estado IN ('Registrado', 'En evaluación', 'En seguimiento', 'Cerrado'))
);

-- Índices para la búsqueda con filtros y orden (HU-15)
CREATE INDEX ix_sismo_fecha_hora ON sismo (fecha_hora);
CREATE INDEX ix_sismo_magnitud ON sismo (magnitud);
CREATE INDEX ix_sismo_estado ON sismo (estado);

-- ---------------------------------------------------------------------
-- Afectaciones y seguimiento: Sismo 1 ── N Reporte, Sismo 1 ── N Seguimiento
-- Al eliminar un sismo se eliminan también sus reportes y seguimientos.
-- ---------------------------------------------------------------------
CREATE TABLE reporte_afectacion (
    id                 BIGINT       PRIMARY KEY AUTO_INCREMENT,
    sismo_id           BIGINT       NOT NULL,
    ubicacion          VARCHAR(150) NOT NULL,
    fecha_hora         DATETIME     NOT NULL,
    tipo_afectacion    VARCHAR(50)  NOT NULL,
    descripcion        VARCHAR(500) NOT NULL,
    nivel              VARCHAR(20)  NOT NULL,
    personas_afectadas INT          NOT NULL DEFAULT 0,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'Pendiente',
    CONSTRAINT fk_reporte_sismo FOREIGN KEY (sismo_id) REFERENCES sismo (id) ON DELETE CASCADE,
    CONSTRAINT chk_reporte_personas CHECK (personas_afectadas >= 0),
    CONSTRAINT chk_reporte_estado CHECK (estado IN ('Pendiente', 'Verificado', 'Cerrado'))
);

CREATE TABLE seguimiento_sismo (
    id                  BIGINT       PRIMARY KEY AUTO_INCREMENT,
    sismo_id            BIGINT       NOT NULL,
    fecha_actualizacion DATETIME     NOT NULL,
    responsable         VARCHAR(100) NOT NULL,
    estado              VARCHAR(20)  NOT NULL,
    observaciones       VARCHAR(500) NOT NULL,
    acciones_realizadas VARCHAR(500) NOT NULL,
    CONSTRAINT fk_seguimiento_sismo FOREIGN KEY (sismo_id) REFERENCES sismo (id) ON DELETE CASCADE
);
