-- =========================================
-- DESACTIVAR CHECKS (para evitar errores de FK)
-- =========================================
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- ROLES
-- =========================================
CREATE TABLE IF NOT EXISTS rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100)
);

INSERT IGNORE INTO rol (id_rol, nombre, descripcion) VALUES
(1, 'ADMIN', 'Administrador del sistema'),
(2, 'CLIENTE', 'Usuario que solicita trabajos'),
(3, 'TRABAJADOR', 'Usuario que realiza trabajos');

-- =========================================
-- USUARIOS
-- =========================================
CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellidos VARCHAR(150),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    id_rol INT NOT NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (id_rol)
        REFERENCES rol(id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

INSERT IGNORE INTO usuario
(id_usuario, nombre, apellidos, username, email, password, telefono, id_rol)
VALUES
(1, 'Admin', 'Sistema', 'admin', 'admin@duit.com', 'admin123', '600000000', 1),
(2, 'Carlos', 'Gómez', 'carlos_cliente', 'carlos@correo.com', 'cliente123', '600000001', 2),
(3, 'Laura', 'Martínez', 'laura_trabajadora', 'laura@correo.com', 'trabajador123', '600000002', 3);

-- =========================================
-- DIRECCIONES
-- =========================================
CREATE TABLE IF NOT EXISTS direccion (
    id_direccion INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    codigo_postal VARCHAR(10),

    CONSTRAINT fk_direccion_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario(id_usuario)
        ON DELETE CASCADE
);

INSERT IGNORE INTO direccion
(id_direccion, id_usuario, direccion, ciudad, codigo_postal)
VALUES
(1, 2, 'Calle Mayor 10', 'Madrid', '28001'),
(2, 3, 'Avenida del Trabajo 5', 'Madrid', '28010');

-- =========================================
-- CATEGORÍAS
-- =========================================
CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(200)
);

INSERT IGNORE INTO categoria (id_categoria, nombre, descripcion) VALUES
(1, 'Fontanería', 'Trabajos de fontanería'),
(2, 'Electricidad', 'Trabajos eléctricos'),
(3, 'Carpintería', 'Trabajos de carpintería'),
(4, 'Pintura', 'Trabajos de pintura');

-- =========================================
-- SOLICITUDES
-- =========================================
CREATE TABLE IF NOT EXISTS solicitud (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_categoria INT NOT NULL,
    titulo VARCHAR(150) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha_solicitud DATETIME DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('ABIERTA','ASIGNADA','FINALIZADA','CANCELADA') DEFAULT 'ABIERTA',

    CONSTRAINT fk_solicitud_cliente
        FOREIGN KEY (id_cliente)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_solicitud_categoria
        FOREIGN KEY (id_categoria)
        REFERENCES categoria(id_categoria)
);

INSERT IGNORE INTO solicitud
(id_solicitud, id_cliente, id_categoria, titulo, descripcion)
VALUES
(1, 2, 2, 'Reparar enchufe',
 'El enchufe del salón no funciona y salta el diferencial.');

-- =========================================
-- TRABAJOS
-- =========================================
CREATE TABLE IF NOT EXISTS trabajo (
    id_trabajo INT AUTO_INCREMENT PRIMARY KEY,
    id_solicitud INT NOT NULL,
    id_trabajador INT NOT NULL,
    precio_acordado DECIMAL(8,2),
    fecha_inicio DATETIME,
    fecha_fin DATETIME,
    estado ENUM('EN_PROCESO','FINALIZADO','CANCELADO') DEFAULT 'EN_PROCESO',

    CONSTRAINT fk_trabajo_solicitud
        FOREIGN KEY (id_solicitud)
        REFERENCES solicitud(id_solicitud),

    CONSTRAINT fk_trabajo_trabajador
        FOREIGN KEY (id_trabajador)
        REFERENCES usuario(id_usuario),

    CONSTRAINT uq_trabajo_solicitud UNIQUE (id_solicitud)
);

INSERT IGNORE INTO trabajo
(id_trabajo, id_solicitud, id_trabajador, precio_acordado,
 fecha_inicio, fecha_fin, estado)
VALUES
(1, 1, 3, 45.00, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'FINALIZADO');

-- =========================================
-- VALORACIONES
-- =========================================
CREATE TABLE IF NOT EXISTS valoracion (
    id_valoracion INT AUTO_INCREMENT PRIMARY KEY,
    id_trabajo INT NOT NULL,
    id_emisor INT NOT NULL,
    id_receptor INT NOT NULL,
    tipo ENUM('CLIENTE_A_TRABAJADOR','TRABAJADOR_A_CLIENTE') NOT NULL,
    puntuacion TINYINT NOT NULL,
    comentario VARCHAR(300),
    fecha_valoracion DATETIME DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_valoracion_trabajo
        FOREIGN KEY (id_trabajo)
        REFERENCES trabajo(id_trabajo),

    CONSTRAINT fk_valoracion_emisor
        FOREIGN KEY (id_emisor)
        REFERENCES usuario(id_usuario),

    CONSTRAINT fk_valoracion_receptor
        FOREIGN KEY (id_receptor)
        REFERENCES usuario(id_usuario)
);

INSERT IGNORE INTO valoracion
(id_valoracion, id_trabajo, id_emisor, id_receptor, tipo, puntuacion, comentario)
VALUES
(1, 1, 2, 3, 'CLIENTE_A_TRABAJADOR', 5, 'Trabajo rápido y muy profesional.'),
(2, 1, 3, 2, 'TRABAJADOR_A_CLIENTE', 5, 'Cliente puntual y trato excelente.');

-- =========================================
-- REACTIVAR CHECKS
-- =========================================
SET FOREIGN_KEY_CHECKS = 1;
