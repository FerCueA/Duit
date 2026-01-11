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
  nombre VARCHAR(50) NOT NULL,
  descripcion VARCHAR(100) NULL
) ENGINE=InnoDB;

-- =========================
-- TABLA: usuario
-- =========================
CREATE TABLE usuario (
  id_usuario INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  apellidos VARCHAR(150) NULL,
  username VARCHAR(50) NOT NULL,
  email VARCHAR(100) NOT NULL,
  password VARCHAR(255) NOT NULL,
  telefono VARCHAR(20) NULL,
  id_rol INT NOT NULL,
  activo TINYINT(1) NOT NULL DEFAULT 1,
  fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE INDEX idx_usuario_rol ON usuario(id_rol);
CREATE INDEX idx_usuario_activo ON usuario(activo);

-- =========================
-- TABLA: direccion
-- =========================
CREATE TABLE direccion (
  id_direccion INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  direccion VARCHAR(200) NOT NULL,
  ciudad VARCHAR(100) NOT NULL,
  codigo_postal VARCHAR(10) NOT NULL
) ENGINE=InnoDB;

CREATE INDEX idx_direccion_usuario ON direccion(id_usuario);

-- =========================
-- TABLA: categoria
-- =========================
CREATE TABLE categoria (
  id_categoria INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(200) NULL
) ENGINE=InnoDB;

-- =========================
-- TABLA: solicitud
-- =========================
CREATE TABLE solicitud (
  id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
  id_cliente INT NOT NULL,
  id_categoria INT NOT NULL,
  id_direccion INT NOT NULL,
  titulo VARCHAR(150) NOT NULL,
  descripcion TEXT NOT NULL,
  fecha_solicitud DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  estado ENUM('ABIERTA','CERRADA','CANCELADA') NOT NULL DEFAULT 'ABIERTA'
) ENGINE=InnoDB;

CREATE INDEX idx_solicitud_cliente ON solicitud(id_cliente);
CREATE INDEX idx_solicitud_categoria ON solicitud(id_categoria);
CREATE INDEX idx_solicitud_direccion ON solicitud(id_direccion);
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
  estado ENUM('PENDIENTE','ACEPTADA','RECHAZADA','CANCELADA') NOT NULL DEFAULT 'PENDIENTE'
) ENGINE=InnoDB;

CREATE INDEX idx_postulacion_solicitud ON postulacion(id_solicitud);
CREATE INDEX idx_postulacion_profesional ON postulacion(id_profesional);
CREATE INDEX idx_postulacion_estado ON postulacion(estado);

-- =========================
-- TABLA: trabajo
-- =========================
CREATE TABLE trabajo (
  id_trabajo INT AUTO_INCREMENT PRIMARY KEY,
  id_postulacion INT NOT NULL,
  precio_acordado DECIMAL(8,2) NULL,
  fecha_inicio DATETIME NULL,
  fecha_fin DATETIME NULL,
  estado ENUM('EN_PROCESO','FINALIZADO','CANCELADO') NOT NULL DEFAULT 'EN_PROCESO'
) ENGINE=InnoDB;

CREATE INDEX idx_trabajo_estado ON trabajo(estado);
CREATE INDEX idx_trabajo_fechas ON trabajo(fecha_inicio, fecha_fin);

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
  fecha_valoracion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE INDEX idx_valoracion_trabajo ON valoracion(id_trabajo);
CREATE INDEX idx_valoracion_emisor ON valoracion(id_emisor);
CREATE INDEX idx_valoracion_receptor ON valoracion(id_receptor);
CREATE INDEX idx_valoracion_fecha ON valoracion(fecha_valoracion);

-- =========================
-- TABLA: datos
-- =========================

INSERT INTO rol (nombre, descripcion) VALUES
('ADMIN','Administrador del sistema'),
('CLIENTE','Publica solicitudes'),
('PROFESIONAL','Se postula a trabajos');

INSERT INTO usuario
(nombre, apellidos, username, email, password, id_rol)
VALUES
(
  'Admin',
  'Sistema',
  'admin',
  'admin@marketplace.com',
  'HASH_admin',
  (SELECT id_rol FROM rol WHERE nombre='ADMIN')
);