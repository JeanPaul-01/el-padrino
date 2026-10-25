-- =====================================================
-- Barberia El Padrino - Script para MySQL 8
-- Ejecutar en MySQL Workbench con el usuario root
-- =====================================================

CREATE DATABASE IF NOT EXISTS barberia_el_padrino
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE barberia_el_padrino;

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    estado BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    correo VARCHAR(100),
    fecha_registro DATE NOT NULL DEFAULT (CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS barbero (
    id_barbero INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    especialidad VARCHAR(100),
    estado BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS servicio (
    id_servicio INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    estado BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS producto (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria VARCHAR(100),
    estado BIT(1) NOT NULL DEFAULT b'1'
);

CREATE TABLE IF NOT EXISTS inventario (
    id_inventario INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    stock_actual INT NOT NULL DEFAULT 0,
    stock_minimo INT NOT NULL DEFAULT 0,
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_inventario_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);

CREATE TABLE IF NOT EXISTS cita (
    id_cita INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_barbero INT NOT NULL,
    id_servicio INT NOT NULL,
    fecha_hora DATETIME NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
    observaciones VARCHAR(255),
    CONSTRAINT FK_cita_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente(id_cliente),
    CONSTRAINT FK_cita_barbero  FOREIGN KEY (id_barbero)  REFERENCES barbero(id_barbero),
    CONSTRAINT FK_cita_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

-- Tabla venta (faltaba en el script original; detalle_venta depende de ella)
CREATE TABLE IF NOT EXISTS venta (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    id_barbero INT NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    descuento DECIMAL(10,2) NOT NULL DEFAULT 0,
    CONSTRAINT FK_venta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT FK_venta_barbero FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);

CREATE TABLE IF NOT EXISTS detalle_venta (
    id_detalle INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT NULL,
    id_servicio INT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    CONSTRAINT FK_detalle_venta_venta    FOREIGN KEY (id_venta)    REFERENCES venta(id_venta),
    CONSTRAINT FK_detalle_venta_producto FOREIGN KEY (id_producto) REFERENCES producto(id_producto),
    CONSTRAINT FK_detalle_venta_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE IF NOT EXISTS fidelizacion (
    id_fidelizacion INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    cortes_realizados INT NOT NULL DEFAULT 0,
    cortes_necesarios INT NOT NULL DEFAULT 5,
    beneficio VARCHAR(100) NOT NULL DEFAULT 'Corte gratis',
    fecha_actualizacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT FK_fidelizacion_cliente FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

-- Usuario de la aplicacion (si ya existe, solo se le dan permisos)
CREATE USER IF NOT EXISTS 'elpadrino_app'@'localhost' IDENTIFIED BY 'Padrino2026@App';
GRANT ALL PRIVILEGES ON barberia_el_padrino.* TO 'elpadrino_app'@'localhost';
FLUSH PRIVILEGES;

SHOW TABLES;
