-- =====================================================================
--  Sistema de gestión integral - Barbería "El Padrino"
--  Script de creación de la base de datos (MySQL 8)
--  Diseño físico según la sección 8.4 del informe, con los ajustes de la Tabla 18:
--    1) id_barbero en VENTA   2) descuento en VENTA   3) categoria en PRODUCTO
--    4) venta.fecha como DATETIME   5) stock mínimo por producto (RF-08)
--  Ejecutar en MySQL Workbench o con: mysql -u root -p < BD_BarberiaElPadrino.sql.sql
-- =====================================================================
SET NAMES utf8mb4;

DROP DATABASE IF EXISTS barberia_el_padrino;
CREATE DATABASE barberia_el_padrino
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_spanish_ci;
USE barberia_el_padrino;

-- ---------------------------------------------------------------------
-- USUARIO: personal que accede al sistema (administrador o recepcionista)
-- ---------------------------------------------------------------------
CREATE TABLE usuario (
    id_usuario      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_usuario  VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL COMMENT 'Cifrada con PBKDF2-SHA256',
    rol             VARCHAR(20)  NOT NULL,
    estado          BIT(1)       NOT NULL DEFAULT 1,
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMINISTRADOR', 'RECEPCIONISTA'))
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- CLIENTE
-- ---------------------------------------------------------------------
CREATE TABLE cliente (
    id_cliente      INT AUTO_INCREMENT PRIMARY KEY,
    nombres         VARCHAR(100) NOT NULL,
    telefono        VARCHAR(15)  NOT NULL UNIQUE,
    correo          VARCHAR(100),
    fecha_registro  DATE         NOT NULL
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- BARBERO
-- ---------------------------------------------------------------------
CREATE TABLE barbero (
    id_barbero      INT AUTO_INCREMENT PRIMARY KEY,
    nombres         VARCHAR(100) NOT NULL,
    telefono        VARCHAR(15),
    especialidad    VARCHAR(100),
    estado          BIT(1)       NOT NULL DEFAULT 1
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- SERVICIO
-- ---------------------------------------------------------------------
CREATE TABLE servicio (
    id_servicio     INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL UNIQUE,
    descripcion     VARCHAR(255),
    precio          DECIMAL(10,2) NOT NULL,
    estado          BIT(1)        NOT NULL DEFAULT 1,
    CONSTRAINT ck_servicio_precio CHECK (precio >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- PRODUCTO
-- ---------------------------------------------------------------------
CREATE TABLE producto (
    id_producto     INT AUTO_INCREMENT PRIMARY KEY,
    nombre          VARCHAR(100)  NOT NULL UNIQUE,
    descripcion     VARCHAR(255),
    categoria       VARCHAR(50),
    precio          DECIMAL(10,2) NOT NULL,
    stock_minimo    INT           NOT NULL DEFAULT 0,
    estado          BIT(1)        NOT NULL DEFAULT 1,
    CONSTRAINT ck_producto_precio CHECK (precio >= 0),
    CONSTRAINT ck_producto_minimo CHECK (stock_minimo >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- INVENTARIO: stock actual de cada producto (un registro por producto)
-- ---------------------------------------------------------------------
CREATE TABLE inventario (
    id_inventario        INT AUTO_INCREMENT PRIMARY KEY,
    id_producto          INT      NOT NULL UNIQUE,
    stock_actual         INT      NOT NULL DEFAULT 0,
    fecha_actualizacion  DATETIME NOT NULL,
    CONSTRAINT fk_inventario_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT ck_inventario_stock CHECK (stock_actual >= 0)
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- CITA
-- ---------------------------------------------------------------------
CREATE TABLE cita (
    id_cita         INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente      INT          NOT NULL,
    id_barbero      INT          NOT NULL,
    id_servicio     INT          NOT NULL,
    fecha           DATE         NOT NULL,
    hora            TIME         NOT NULL,
    estado          VARCHAR(20)  NOT NULL DEFAULT 'PENDIENTE',
    observaciones   VARCHAR(255),
    fecha_registro  DATETIME     NOT NULL,
    CONSTRAINT fk_cita_cliente  FOREIGN KEY (id_cliente)  REFERENCES cliente (id_cliente),
    CONSTRAINT fk_cita_barbero  FOREIGN KEY (id_barbero)  REFERENCES barbero (id_barbero),
    CONSTRAINT fk_cita_servicio FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio),
    CONSTRAINT ck_cita_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'ATENDIDA', 'CANCELADA'))
) ENGINE = InnoDB;

-- Índice recomendado en RNF-03 para los reportes y la validación de cruces de horario
CREATE INDEX ix_cita_fecha_barbero ON cita (fecha, id_barbero);

-- ---------------------------------------------------------------------
-- VENTA
-- ---------------------------------------------------------------------
CREATE TABLE venta (
    id_venta        INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente      INT           NOT NULL,
    id_usuario      INT           NOT NULL,
    id_barbero      INT           NOT NULL,
    fecha           DATETIME      NOT NULL,
    descuento       DECIMAL(10,2) NOT NULL DEFAULT 0,
    total           DECIMAL(10,2) NOT NULL,
    metodo_pago     VARCHAR(20)   NOT NULL,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente),
    CONSTRAINT fk_venta_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_venta_barbero FOREIGN KEY (id_barbero) REFERENCES barbero (id_barbero),
    CONSTRAINT ck_venta_metodo CHECK (metodo_pago IN ('EFECTIVO', 'YAPE', 'PLIN', 'TARJETA')),
    CONSTRAINT ck_venta_montos CHECK (descuento >= 0 AND total >= 0)
) ENGINE = InnoDB;

CREATE INDEX ix_venta_fecha ON venta (fecha);

-- ---------------------------------------------------------------------
-- DETALLE_VENTA: cada línea es un servicio o un producto
-- ---------------------------------------------------------------------
CREATE TABLE detalle_venta (
    id_detalle       INT AUTO_INCREMENT PRIMARY KEY,
    id_venta         INT           NOT NULL,
    id_servicio      INT           NULL,
    id_producto      INT           NULL,
    cantidad         INT           NOT NULL DEFAULT 1,
    precio_unitario  DECIMAL(10,2) NOT NULL,
    subtotal         DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalle_venta    FOREIGN KEY (id_venta)    REFERENCES venta (id_venta),
    CONSTRAINT fk_detalle_servicio FOREIGN KEY (id_servicio) REFERENCES servicio (id_servicio),
    CONSTRAINT fk_detalle_producto FOREIGN KEY (id_producto) REFERENCES producto (id_producto),
    CONSTRAINT ck_detalle_item CHECK ((id_servicio IS NULL) <> (id_producto IS NULL)),
    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0)
) ENGINE = InnoDB;

CREATE INDEX ix_detalle_venta ON detalle_venta (id_venta);

-- ---------------------------------------------------------------------
-- FIDELIZACION: cada 5 cortes pagados, el 6.º es gratis
-- ---------------------------------------------------------------------
CREATE TABLE fidelizacion (
    id_fidelizacion       INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente            INT      NOT NULL UNIQUE,
    cortes_acumulados     INT      NOT NULL DEFAULT 0,
    puntos                INT      NOT NULL DEFAULT 0,
    ultima_actualizacion  DATETIME NOT NULL,
    CONSTRAINT fk_fidelizacion_cliente FOREIGN KEY (id_cliente) REFERENCES cliente (id_cliente)
) ENGINE = InnoDB;
