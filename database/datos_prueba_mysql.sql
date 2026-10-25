USE barberia_el_padrino;

INSERT INTO usuario (nombre_usuario, password, rol, estado)
SELECT 'admin', 'admin123', 'Administrador', 1
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE nombre_usuario = 'admin');

INSERT INTO barbero (nombres, telefono, especialidad, estado)
SELECT * FROM (
    SELECT 'Carlos Ramirez' AS nombres, '999111222' AS telefono, 'Cortes clásicos' AS especialidad, 1 AS estado
    UNION ALL SELECT 'Luis Mendoza', '999333444', 'Fade y modernos', 1
) t
WHERE NOT EXISTS (SELECT 1 FROM barbero);

INSERT INTO servicio (nombre, descripcion, precio, estado)
SELECT * FROM (
    SELECT 'Corte clásico' AS nombre, 'Corte tradicional de caballero' AS descripcion, 25.00 AS precio, 1 AS estado
    UNION ALL SELECT 'Corte fade', 'Degradado moderno', 30.00, 1
    UNION ALL SELECT 'Barba', 'Perfilado y arreglo de barba', 15.00, 1
) t
WHERE NOT EXISTS (SELECT 1 FROM servicio);

INSERT INTO cliente (nombres, telefono, correo)
SELECT 'Cliente de prueba', '999888777', 'cliente@ejemplo.com'
WHERE NOT EXISTS (SELECT 1 FROM cliente);

INSERT INTO producto (nombre, descripcion, precio, stock, categoria, estado)
SELECT * FROM (
    SELECT 'Cera para cabello' AS nombre, 'Cera de fijación para cabello' AS descripcion, 25.00 AS precio, 20 AS stock, 'Cabello' AS categoria, 1 AS estado
    UNION ALL SELECT 'Aceite para barba', 'Aceite para cuidado de barba', 30.00, 15, 'Barba', 1
    UNION ALL SELECT 'Shampoo masculino', 'Shampoo de uso profesional', 22.00, 10, 'Cabello', 1
) t
WHERE NOT EXISTS (SELECT 1 FROM producto);
