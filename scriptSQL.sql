-- =========================
-- CREACIÓN DE LA BASE DE DATOS
-- =========================
CREATE DATABASE IF NOT EXISTS gestion_trabajos
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_spanish_ci;

USE gestion_trabajos;

-- =========================
-- TABLA ROL
-- =========================
CREATE TABLE rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE
);

-- =========================
-- TABLA USUARIO
-- =========================
CREATE TABLE usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ACTIVO','BLOQUEADO') NOT NULL DEFAULT 'ACTIVO',
    id_rol INT NOT NULL,
    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
);

-- =========================
-- TABLA TIPO_TRABAJO
-- =========================
CREATE TABLE tipo_trabajo (
    id_tipo_trabajo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
);

-- =========================
-- TABLA TRABAJO
-- =========================
CREATE TABLE trabajo (
    id_trabajo INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_finalizacion DATETIME,
    estado ENUM('ABIERTO','ASIGNADO','FINALIZADO') NOT NULL DEFAULT 'ABIERTO',
    id_cliente INT NOT NULL,
    id_trabajador INT,
    id_tipo_trabajo INT NOT NULL,
    CONSTRAINT fk_trabajo_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_trabajo_trabajador
        FOREIGN KEY (id_trabajador)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_trabajo_tipo
        FOREIGN KEY (id_tipo_trabajo)
        REFERENCES tipo_trabajo(id_tipo_trabajo)
);

-- =========================
-- TABLA MENSAJE
-- =========================
CREATE TABLE mensaje (
    id_mensaje INT AUTO_INCREMENT PRIMARY KEY,
    contenido TEXT NOT NULL,
    fecha_envio DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_trabajo INT NOT NULL,
    id_emisor INT NOT NULL,
    id_receptor INT NOT NULL,
    CONSTRAINT fk_mensaje_trabajo
        FOREIGN KEY (id_trabajo)
        REFERENCES trabajo(id_trabajo),
    CONSTRAINT fk_mensaje_emisor
        FOREIGN KEY (id_emisor)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_mensaje_receptor
        FOREIGN KEY (id_receptor)
        REFERENCES usuario(id_usuario)
);

-- =========================
-- TABLA VALORACION
-- =========================
CREATE TABLE valoracion (
    id_valoracion INT AUTO_INCREMENT PRIMARY KEY,
    puntuacion INT NOT NULL CHECK (puntuacion BETWEEN 1 AND 5),
    comentario VARCHAR(255),
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_trabajo INT NOT NULL,
    id_autor INT NOT NULL,
    id_destinatario INT NOT NULL,
    CONSTRAINT fk_valoracion_trabajo
        FOREIGN KEY (id_trabajo)
        REFERENCES trabajo(id_trabajo),
    CONSTRAINT fk_valoracion_autor
        FOREIGN KEY (id_autor)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_valoracion_destinatario
        FOREIGN KEY (id_destinatario)
        REFERENCES usuario(id_usuario)
);

-- =========================
-- TABLA ACTIVIDAD
-- =========================
CREATE TABLE actividad (
    id_actividad INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(255) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario INT NOT NULL,
    id_trabajo INT,
    CONSTRAINT fk_actividad_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario),
    CONSTRAINT fk_actividad_trabajo
        FOREIGN KEY (id_trabajo)
        REFERENCES trabajo(id_trabajo)
);
