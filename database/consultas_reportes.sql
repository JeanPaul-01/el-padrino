-- =====================================================================
--  Consultas SQL de los reportes R01 a R07 (Anexo B del informe) - MySQL 8
--  Reemplace las variables @desde, @hasta, @id_barbero y @id_servicio.
--  Un valor NULL equivale a "Todos".
--  El sistema calcula estos mismos reportes en Java (ReporteService).
-- =====================================================================
SET NAMES utf8mb4;
USE barberia_el_padrino;

SET @desde = DATE_SUB(CURDATE(), INTERVAL 30 DAY);
SET @hasta = CURDATE();
SET @id_barbero = NULL;
SET @id_servicio = NULL;

-- R01 - Ingresos por periodo (agrupados por día)
SELECT DATE(v.fecha) AS dia,
       COUNT(*)      AS num_ventas,
       SUM(v.total)  AS ingresos
FROM venta v
WHERE v.fecha >= @desde
  AND v.fecha < @hasta + INTERVAL 1 DAY
  AND (@id_barbero IS NULL OR v.id_barbero = @id_barbero)
  AND (@id_servicio IS NULL OR EXISTS (SELECT 1 FROM detalle_venta d
                                       WHERE d.id_venta = v.id_venta AND d.id_servicio = @id_servicio))
GROUP BY DATE(v.fecha)
ORDER BY dia;

-- R01 - Totales de la tarjeta "Total del periodo"
SELECT COALESCE(SUM(CASE WHEN d.id_servicio IS NOT NULL THEN d.cantidad END), 0) AS servicios,
       COALESCE(SUM(CASE WHEN d.id_producto IS NOT NULL THEN d.cantidad END), 0) AS productos
FROM venta v
JOIN detalle_venta d ON d.id_venta = v.id_venta
WHERE v.fecha >= @desde AND v.fecha < @hasta + INTERVAL 1 DAY
  AND (@id_barbero IS NULL OR v.id_barbero = @id_barbero);

-- R02 - Ingresos por barbero
SELECT b.nombres AS barbero,
       COUNT(DISTINCT v.id_venta) AS atenciones,
       SUM(v.total) AS ingresos,
       ROUND(100 * SUM(v.total) / SUM(SUM(v.total)) OVER (), 1) AS porcentaje
FROM venta v
JOIN barbero b ON b.id_barbero = v.id_barbero
WHERE v.fecha >= @desde AND v.fecha < @hasta + INTERVAL 1 DAY
GROUP BY b.id_barbero, b.nombres
ORDER BY ingresos DESC;

-- R03 - Servicios más solicitados
SELECT s.nombre AS servicio,
       SUM(d.cantidad) AS cantidad,
       SUM(d.subtotal) AS ingresos
FROM detalle_venta d
JOIN venta v    ON v.id_venta = d.id_venta
JOIN servicio s ON s.id_servicio = d.id_servicio
WHERE v.fecha >= @desde AND v.fecha < @hasta + INTERVAL 1 DAY
  AND (@id_barbero IS NULL OR v.id_barbero = @id_barbero)
GROUP BY s.id_servicio, s.nombre
ORDER BY cantidad DESC;

-- R04 - Productos con stock bajo
SELECT p.nombre AS producto,
       i.stock_actual AS stock,
       p.stock_minimo AS minimo,
       p.stock_minimo - i.stock_actual AS faltan,
       CASE WHEN i.stock_actual = 0 THEN 'AGOTADO' ELSE 'STOCK BAJO' END AS estado
FROM producto p
JOIN inventario i ON i.id_producto = p.id_producto
WHERE p.estado = 1
  AND i.stock_actual <= p.stock_minimo
ORDER BY i.stock_actual ASC, faltan DESC;

-- R05 - Clientes más frecuentes
SELECT c.nombres AS cliente,
       COUNT(DISTINCT v.id_venta) AS visitas,
       MAX(v.fecha) AS ultima_visita,
       COALESCE(f.cortes_acumulados, 0) AS cortes_acumulados
FROM cliente c
JOIN venta v ON v.id_cliente = c.id_cliente
JOIN detalle_venta d ON d.id_venta = v.id_venta AND d.id_servicio IS NOT NULL
LEFT JOIN fidelizacion f ON f.id_cliente = c.id_cliente
GROUP BY c.id_cliente, c.nombres, f.cortes_acumulados
ORDER BY visitas DESC, ultima_visita DESC
LIMIT 10;

-- R06 - Estado del programa de fidelización (indicadores)
SELECT COUNT(*) AS clientes_en_programa,
       SUM(f.cortes_acumulados >= 5) AS cortes_gratis_disponibles
FROM fidelizacion f;

SELECT COUNT(*) AS canjes_del_mes
FROM venta v
WHERE v.descuento > 0
  AND v.fecha >= DATE_FORMAT(CURDATE(), '%Y-%m-01');

-- R06 - Detalle por cliente
SELECT c.nombres AS cliente,
       LEAST(f.cortes_acumulados, 5) AS cortes,
       ROUND(LEAST(f.cortes_acumulados, 5) / 5 * 100) AS avance_pct,
       CASE WHEN f.cortes_acumulados >= 5 THEN 'CORTE GRATIS DISPONIBLE' ELSE 'EN PROGRESO' END AS estado
FROM fidelizacion f
JOIN cliente c ON c.id_cliente = f.id_cliente
ORDER BY f.cortes_acumulados DESC;

-- R07 - Resumen del día (Dashboard)
SELECT COUNT(*) AS citas_hoy,
       SUM(estado = 'PENDIENTE')  AS pendientes,
       SUM(estado = 'CONFIRMADA') AS confirmadas
FROM cita
WHERE fecha = CURDATE() AND estado <> 'CANCELADA';

SELECT COALESCE(SUM(total), 0) AS ingresos_dia
FROM venta
WHERE fecha >= CURDATE() AND fecha < CURDATE() + INTERVAL 1 DAY;

SELECT COUNT(*) AS productos_stock_critico
FROM producto p
JOIN inventario i ON i.id_producto = p.id_producto
WHERE p.estado = 1 AND i.stock_actual <= p.stock_minimo;
