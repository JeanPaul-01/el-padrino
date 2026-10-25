# Despliegue: Render (aplicación) + Aiven (MySQL)

Arquitectura: un solo contenedor Docker en **Render** (Spring Boot + JSP, war ejecutable)
conectado por SSL a una base **MySQL 8 gratuita en Aiven**. Ambos planes son gratuitos.

```
Navegador ──HTTPS──> Render (Docker: java -jar app.war, perfil "prod") ──SSL──> Aiven MySQL
```

## Archivos involucrados

| Archivo | Propósito |
|---|---|
| `src/main/resources/application.properties` | Configuración base. Lee `PORT`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`; si no existen usa los valores locales. |
| `src/main/resources/application-prod.properties` | Perfil `prod`: credenciales obligatorias por variable de entorno, pool pequeño, cookies seguras, sin detalles de errores. |
| `Dockerfile` | Compila con Maven y ejecuta el war con Java 17 JRE ajustado a 512 MB de RAM. |
| `render.yaml` | Blueprint de Render (servicio web gratuito, Docker, health check en `/login`). |

En local nada cambia: `.\mvnw.cmd clean spring-boot:run` sigue funcionando igual.

## 1. Base de datos en Aiven

1. Crear cuenta en <https://console.aiven.io> → **Create service** → **MySQL** → plan **Free**.
2. En **Overview** copiar: *Host*, *Port*, *User* (`avnadmin`) y *Password*.
3. Conectarse desde **MySQL Workbench** (pestaña SSL: *Use SSL = Require*) con esos datos.
4. Ejecutar, en este orden:
   1. `database/BD_BarberiaElPadrino.sql.sql` (crea la base `barberia_el_padrino`)
   2. `database/datos_prueba.sql`

## 2. Aplicación en Render

1. Hacer merge de esta rama a `main` y subirla a GitHub.
2. En <https://dashboard.render.com> → **New** → **Blueprint** → elegir el repositorio `el-padrino`.
3. Completar las variables que pide:

   | Variable | Valor |
   |---|---|
   | `DB_URL` | `jdbc:mysql://<HOST>:<PORT>/barberia_el_padrino?sslMode=REQUIRED&serverTimezone=America/Lima` |
   | `DB_USERNAME` | `avnadmin` |
   | `DB_PASSWORD` | contraseña de Aiven |

4. Esperar el primer build (≈ 5–8 min). La URL queda como `https://el-padrino-xxxx.onrender.com`.

## 3. Después del primer despliegue

- **Cambiar las contraseñas de `admin` y `recepcion`** desde el módulo Usuarios: los valores de prueba
  están en el repositorio y la aplicación ya es pública.
- El plan gratuito de Render **se suspende tras ~15 min sin tráfico**; la primera visita tarda ~1 min.
  Antes de una presentación, abrir la URL unos minutos antes.
- Cada push a `main` vuelve a desplegar automáticamente.

## Problemas frecuentes

| Síntoma en los logs de Render | Causa |
|---|---|
| `Could not resolve placeholder 'DB_URL'` | Falta una variable de entorno en Render. |
| `Communications link failure` | Host/puerto incorrectos o falta `sslMode=REQUIRED`. |
| `Schema-validation: missing table` | No se ejecutó el script de la base en Aiven. |
| `Too many connections` | Bajar `DB_POOL_SIZE` (por defecto 3). |
