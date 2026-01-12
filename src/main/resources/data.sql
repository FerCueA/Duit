DROP DATABASE IF EXISTS duit_db;
CREATE DATABASE duit_db
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE duit_db;

-- =========================
-- TABLA: rol
-- =========================
CREATE TABLE rol (
  id_rol INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(50) NOT NULL UNIQUE,
  descripcion VARCHAR(100) NULL
) ENGINE=InnoDB;

-- =========================
-- TABLA: direccion
-- =========================
CREATE TABLE direccion (
  id_direccion INT AUTO_INCREMENT PRIMARY KEY,
  direccion VARCHAR(200) NOT NULL,
  ciudad VARCHAR(100) NOT NULL,
  codigo_postal VARCHAR(10) NOT NULL,
  provincia VARCHAR(100) NULL,
  pais VARCHAR(50) NOT NULL DEFAULT 'España'
) ENGINE=InnoDB;

-- =========================
-- TABLA: usuario
-- =========================
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(150) NULL,
  username VARCHAR(50) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  telefono VARCHAR(20) NULL,
  id_rol INT NOT NULL,
  id_direccion INT NULL, -- se completa después del registro
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_usuario_rol
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol),

  CONSTRAINT fk_usuario_direccion
    FOREIGN KEY (id_direccion) REFERENCES direccion(id_direccion)
) ENGINE=InnoDB;

CREATE INDEX idx_usuario_activo ON usuario(activo);

-- =========================
-- TABLA: categoria
-- =========================
CREATE TABLE categoria (
  id_categoria INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL UNIQUE,
  descripcion VARCHAR(200) NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB;

-- =========================
-- TABLA: perfil_profesional
-- =========================
CREATE TABLE perfil_profesional (
  id_profesional INT PRIMARY KEY,
  descripcion TEXT NOT NULL,
  precio_hora DECIMAL(8,2) NOT NULL,
  nif VARCHAR(9) NOT NULL UNIQUE,
  fecha_alta DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_perfil_profesional_usuario
    FOREIGN KEY (id_profesional) REFERENCES usuario(id_usuario)
) ENGINE=InnoDB;

-- =========================
-- TABLA: profesional_categoria (N:M)
-- =========================
CREATE TABLE profesional_categoria (
  id_profesional INT NOT NULL,
  id_categoria INT NOT NULL,

  PRIMARY KEY (id_profesional, id_categoria),

  CONSTRAINT fk_pc_profesional
    FOREIGN KEY (id_profesional) REFERENCES perfil_profesional(id_profesional),

  CONSTRAINT fk_pc_categoria
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE=InnoDB;

-- =========================
-- TABLA: solicitud
-- =========================
CREATE TABLE solicitud (
  id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  id_categoria INT NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  descripcion TEXT NOT NULL,
  fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_actualizacion DATETIME NULL,
  estado ENUM('ABIERTA','CERRADA','CANCELADA') NOT NULL DEFAULT 'ABIERTA',

  CONSTRAINT fk_solicitud_cliente
    FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario),

  CONSTRAINT fk_solicitud_categoria
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
) ENGINE=InnoDB;

CREATE INDEX idx_solicitud_estado ON solicitud(estado);
CREATE INDEX idx_solicitud_fecha ON solicitud(fecha_solicitud);

-- =========================
-- TABLA: postulacion
-- =========================
CREATE TABLE postulacion (
  id_postulacion INT AUTO_INCREMENT PRIMARY KEY,
  id_solicitud INT NOT NULL,
  id_profesional INT NOT NULL,
  mensaje TEXT NULL,
  precio_propuesto DECIMAL(8,2) NULL,
  fecha_postulacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  fecha_respuesta DATETIME NULL,
  estado ENUM('PENDIENTE','ACEPTADA','RECHAZADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE',

  CONSTRAINT fk_postulacion_solicitud
    FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud),

  CONSTRAINT fk_postulacion_profesional
    FOREIGN KEY (id_profesional) REFERENCES usuario(id_usuario),

  CONSTRAINT uq_postulacion_unica
    UNIQUE (id_solicitud, id_profesional)
) ENGINE=InnoDB;

CREATE INDEX idx_postulacion_estado ON postulacion(estado);

-- =========================
-- TABLA: trabajo
-- =========================
CREATE TABLE trabajo (
  id_trabajo INT AUTO_INCREMENT PRIMARY KEY,
  id_postulacion INT NOT NULL UNIQUE,
  precio_acordado DECIMAL(8,2) NULL,
  fecha_inicio DATETIME NULL,
  fecha_fin DATETIME NULL,
  estado ENUM('EN_PROCESO','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'EN_PROCESO',
  observaciones TEXT NULL,

  CONSTRAINT fk_trabajo_postulacion
    FOREIGN KEY (id_postulacion) REFERENCES postulacion(id_postulacion),

  CONSTRAINT chk_fechas_trabajo
    CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;

-- =========================
-- TABLA: valoracion
-- =========================
CREATE TABLE valoracion (
  id_valoracion INT AUTO_INCREMENT PRIMARY KEY,
  id_trabajo INT NOT NULL,
  id_emisor INT NOT NULL,
  id_receptor INT NOT NULL,
  tipo ENUM('CLIENTE_A_PROFESIONAL','PROFESIONAL_A_CLIENTE') NOT NULL,
  puntuacion TINYINT NOT NULL,
  comentario VARCHAR(300) NULL,
  fecha_valoracion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_valoracion_trabajo
    FOREIGN KEY (id_trabajo) REFERENCES trabajo(id_trabajo),

  CONSTRAINT fk_valoracion_emisor
    FOREIGN KEY (id_emisor) REFERENCES usuario(id_usuario),

  CONSTRAINT fk_valoracion_receptor
    FOREIGN KEY (id_receptor) REFERENCES usuario(id_usuario),

  CONSTRAINT chk_puntuacion
    CHECK (puntuacion BETWEEN 1 AND 5),

  CONSTRAINT uq_valoracion_tipo
    UNIQUE (id_trabajo, tipo)
) ENGINE=InnoDB;



