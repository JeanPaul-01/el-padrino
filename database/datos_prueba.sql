-- =====================================================================
--  Datos de prueba - Barbería "El Padrino" (MySQL 8)
--  Ejecutar DESPUÉS de BD_BarberiaElPadrino.sql.sql
--  Las fechas son relativas al día actual para que el Dashboard muestre información.
--
--  Usuarios de prueba:
--    admin     / admin123      (Administrador)
--    recepcion / recepcion123  (Recepcionista)
-- =====================================================================
SET NAMES utf8mb4;
USE barberia_el_padrino;

INSERT INTO usuario (nombre_usuario, password, rol, estado) VALUES
('admin',     'pbkdf2$65536$jjq3DazHCALnhKNP8ZRTXA==$oe/jGjRPQcZFIOzsKs4ng+wNv1tnYspReQztmWgZNgQ=', 'ADMINISTRADOR', 1),
('recepcion', 'pbkdf2$65536$bn+rmZmNo43PDQppPHEB7w==$CT/FyVW6cUSamxrLKbLEGfe9AqwsvtQQnZRIj/N7kxk=', 'RECEPCIONISTA', 1);

INSERT INTO barbero (nombres, telefono, especialidad, estado) VALUES
('Jorge', '987654321', 'Cortes clásicos y navaja', 1),
('Mateo', '976543210', 'Fade y estilos modernos', 1),
('Renzo', '965432109', 'Barba y perfilado', 1);

INSERT INTO servicio (nombre, descripcion, precio, estado) VALUES
('Corte clásico', 'Corte tradicional de caballero',       25.00, 1),
('Corte + barba', 'Corte de cabello y arreglo de barba',   40.00, 1),
('Barba',         'Perfilado y arreglo de barba',          20.00, 1),
('Perfilado',     'Perfilado de cejas y contornos',        15.00, 1);

INSERT INTO producto (nombre, descripcion, categoria, precio, stock_minimo, estado) VALUES
('Loción after shave', 'Loción refrescante para después del afeitado', 'Afeitado', 35.00, 3, 1),
('Shampoo barba',      'Shampoo especial para barba',                  'Barba',    28.00, 4, 1),
('Cera mate',          'Cera de fijación media con acabado mate',      'Cabello',  30.00, 5, 1),
('Aceite para barba',  'Aceite hidratante para barba',                 'Barba',    32.00, 3, 1),
('Pomada brillante',   'Pomada de fijación fuerte con brillo',         'Cabello',  30.00, 4, 1);

INSERT INTO inventario (id_producto, stock_actual, fecha_actualizacion) VALUES
(1, 0,  NOW()),
(2, 1,  NOW()),
(3, 2,  NOW()),
(4, 12, NOW()),
(5, 9,  NOW());

INSERT INTO cliente (nombres, telefono, correo, fecha_registro) VALUES
('Carlos Mendoza', '912345678', 'carlos.mendoza@yutp.edu.pe', DATE_SUB(CURDATE(), INTERVAL 90 DAY)),
('Diego Salas',    '923456789', 'diego.salas@gmail.com',    DATE_SUB(CURDATE(), INTERVAL 75 DAY)),
('Luis Paredes',   '934567890', NULL,                        DATE_SUB(CURDATE(), INTERVAL 60 DAY)),
('Iván Cáceres',   '945678901', 'ivan.caceres@hormail.com',   DATE_SUB(CURDATE(), INTERVAL 45 DAY)),
('Marco Torres',   '956789012', NULL,                        DATE_SUB(CURDATE(), INTERVAL 10 DAY));

-- Citas de hoy y de los próximos días
INSERT INTO cita (id_cliente, id_barbero, id_servicio, fecha, hora, estado, observaciones, fecha_registro) VALUES
(1, 1, 1, CURDATE(),                           '10:00', 'CONFIRMADA', NULL,               NOW()),
(2, 2, 2, CURDATE(),                           '11:30', 'PENDIENTE',  'Reserva por WhatsApp', NOW()),
(3, 3, 3, CURDATE(),                           '16:00', 'CONFIRMADA', NULL,               NOW()),
(4, 1, 2, CURDATE(),                           '18:30', 'PENDIENTE',  NULL,               NOW()),
(5, 2, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:30', 'PENDIENTE',  'Reserva en línea', NOW()),
(1, 3, 4, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '15:00', 'CONFIRMADA', NULL,               NOW());

-- Ventas de los últimos días (id_usuario 2 = recepcion)
INSERT INTO venta (id_cliente, id_usuario, id_barbero, fecha, descuento, total, metodo_pago) VALUES
(1, 2, 1, DATE_SUB(NOW(), INTERVAL 6 DAY),  0.00, 25.00, 'EFECTIVO'),
(2, 2, 2, DATE_SUB(NOW(), INTERVAL 5 DAY),  0.00, 68.00, 'YAPE'),
(3, 2, 3, DATE_SUB(NOW(), INTERVAL 4 DAY),  0.00, 20.00, 'PLIN'),
(4, 2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY), 40.00, 30.00, 'TARJETA'),
(1, 2, 2, DATE_SUB(NOW(), INTERVAL 2 DAY),  0.00, 40.00, 'YAPE'),
(2, 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY),  0.00, 25.00, 'EFECTIVO'),
(5, 2, 3, DATE_SUB(NOW(), INTERVAL 1 DAY),  0.00, 47.00, 'EFECTIVO');

INSERT INTO detalle_venta (id_venta, id_servicio, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1,    NULL, 1, 25.00, 25.00),
(2, 2,    NULL, 1, 40.00, 40.00),
(2, NULL, 2,    1, 28.00, 28.00),
(3, 3,    NULL, 1, 20.00, 20.00),
(4, 2,    NULL, 1, 40.00, 40.00),   -- corte gratis canjeado (descuento 40.00)
(4, NULL, 3,    1, 30.00, 30.00),
(5, 2,    NULL, 1, 40.00, 40.00),
(6, 1,    NULL, 1, 25.00, 25.00),
(7, 4,    NULL, 1, 15.00, 15.00),
(7, NULL, 4,    1, 32.00, 32.00);

INSERT INTO fidelizacion (id_cliente, cortes_acumulados, puntos, ultima_actualizacion) VALUES
(1, 3, 13, NOW()),
(2, 5, 9,  NOW()),   -- tiene un corte gratis disponible
(3, 2, 7,  NOW()),
(4, 0, 10, NOW()),
(5, 1, 1,  NOW());
