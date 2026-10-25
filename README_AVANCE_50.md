# Barbería "El Padrino" — Sistema de gestión integral

Implementación de la **Alternativa 1 (Aplicación Web)** elegida en el informe:
**Stack Tecnológico:**
**Java 17 + Spring Boot + Spring Data JPA + JSP + Bootstrap 5 + MySQL**, con arquitectura en capas
**Controller → Service → Repository**.

## Cómo ejecutarlo

1. **Base de datos (MySQL 8)**: en MySQL Workbench abra y ejecute, en este orden:
   1. `database/BD_BarberiaElPadrino.sql.sql` (crea la base `barberia_el_padrino` y sus 10 tablas)
   2. `database/datos_prueba.sql` (barberos, servicios, productos, clientes, citas y ventas de ejemplo)
2. **Conexión**: en `src/main/resources/application.properties` cambie `spring.datasource.username`
   y `spring.datasource.password` por los de su MySQL.
3. **Ejecutar** (VS Code, terminal en la carpeta del proyecto):
   ```
   .\mvnw.cmd clean spring-boot:run
   ```
4. Abrir `http://localhost:8080`

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | Administrador |
| recepcion | recepcion123 | Recepcionista |

La reserva en línea para clientes (sin iniciar sesión) está en `http://localhost:8080/reserva`.

## Pantallas (sección 8.5.2 del informe)

| N.º | Pantalla | Ruta |
|---|---|---|
| 1 | Login | `/login` |
| 2 | Dashboard (reporte R07) | `/dashboard` |
| 3 | Calendario de citas (semana, filtro por barbero, colores por estado) | `/citas` |
| 4 | Nueva cita (solo horarios libres) | `/citas/nueva` |
| 5 | Reserva en línea | `/reserva` |
| 6 | Clientes (buscador) | `/clientes` |
| 7 | Perfil e historial del cliente + tarjeta de fidelización | `/clientes/{id}` |
| 8 | Inventario (alerta de stock bajo) | `/inventario` |
| 9 | Registro de producto / ajuste de stock | `/productos/nuevo`, `/inventario/{id}/ajuste` |
| 10 | Registro de venta y pago | `/ventas/nueva` |
| 11 | Programa de fidelización (R06) | `/fidelizacion` |
| 12 | Reporte de ingresos (R01, R02, R03 + exportar a Excel) | `/reportes/ingresos` |
| 13 | Reportes de stock bajo y clientes frecuentes (R04, R05) | `/reportes/stock-clientes` |

Además: mantenimiento de servicios, barberos y usuarios (este último solo para el administrador).

## Reglas de negocio implementadas (capa Service)

- **Citas**: no permite dos citas activas del mismo barbero a la misma hora; horario de lunes a sábado
  de 9:00 a 20:00 en turnos de 30 minutos; no acepta fechas pasadas; solo se confirman o cancelan
  citas pendientes/confirmadas. "Atender" una cita abre la venta y la marca como **Atendida**.
- **Clientes**: celular de 9 dígitos que empieza con 9 y sin duplicados.
- **Inventario**: precio y stock ≥ 0, nombres de producto únicos, alerta cuando el stock actual ≤ stock mínimo.
- **Ventas**: varias líneas de servicios y productos por venta, cálculo automático del total,
  descuento de stock (no vende sin stock) y método de pago.
- **Fidelización**: cada servicio pagado suma un corte; al llegar a 5, el siguiente servicio se descuenta
  automáticamente (corte gratis) y la tarjeta se reinicia.
- **Seguridad**: contraseñas cifradas con PBKDF2, sesión obligatoria, roles Administrador/Recepcionista.

### Funcionalidades del módulo de clientes

El módulo de gestión de clientes permite:

- Consultar los clientes registrados en el sistema.
- Buscar clientes para facilitar su identificación.
- Acceder al perfil de cada cliente y consultar su historial.
- Visualizar la tarjeta de fidelización asociada al cliente.

La ruta principal del módulo es `/clientes`, mientras que el perfil individual se consulta mediante `/clientes/{id}`.

## Estructura

```
src/main/java/com/elpadrino/
  model/        10 entidades JPA (tablas del diseño físico) + enums
  repository/   Spring Data JPA
  service/      reglas de negocio y reportes R01–R07
  controller/   controladores MVC que devuelven vistas JSP
  dto/, util/, config/, exception/
src/main/webapp/WEB-INF/views/   vistas JSP con Bootstrap 5 (incluido en static/vendor, no requiere CDN)
src/test/java/                   pruebas unitarias (mvnw.cmd test)
database/                        scripts MySQL y consultas SQL del Anexo
```

## Proporción de Java

Sin contar librerías externas: Java ≈ 64 % de las líneas de código y JSP ≈ 34 %
(JSP también es tecnología Java). El requisito de "al menos 50 % de Java" se cumple.
