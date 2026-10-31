# Sistema de Gestión Integral – Barbería "El Padrino"

Curso Integrador I: Sistemas Software · UTP · Sección 21922 · Avance del Proyecto Final 3

Aplicación web para digitalizar la gestión de citas, clientes, inventario, ventas, fidelización y reportes de la barbería "El Padrino".

## Integrantes y módulos

| Integrante | Módulo | Rama |
|---|---|---|
| Jean Paul Velasquez Chahua (líder y responsable del repositorio) | Gestión de clientes | `feature/gestion-clientes` |
| Jesus Andres Calderon Gutierrez | Inventario y productos | `feature/gestion-inventario` |
| Deivith Flores Travezaño | Ventas y pagos | `feature/gestion-ventas` |
| Jair Alexander Alban Marquez | Gestión de citas | `feature/gestion-citas` |
| Tonny Joel Lozano Rodriguez | Servicios y barberos | `feature/gestion-servicios` |

## Tecnologías

Java 17 · Spring Boot · Spring Data JPA · JSP + JSTL + Bootstrap · MySQL · Maven

Librerías de apoyo: Google Guava, Apache POI, Apache Commons Lang y Logback.

## Arquitectura

Patrón MVC con capa DAO:

```
src/main/java/com/elpadrino
├── controller   Controladores web (C de MVC)
├── service      Reglas de negocio
├── repository   Capa DAO: interfaces Spring Data JPA
├── model        Entidades JPA (M de MVC)
├── dto          Objetos de los formularios
├── exception    ReglaNegocioException y manejo de errores
├── config       Configuración (seguridad, web)
└── util         ValidacionUtils, ExcelExporter
src/main/webapp/WEB-INF/views   Vistas JSP (V de MVC)
```

## Cómo ejecutar

```bash
.\mvnw spring-boot:run          # levantar la aplicación
.\mvnw test                     # ejecutar las pruebas unitarias y de integración
.\mvnw javadoc:javadoc          # generar la documentación en target/reports/apidocs
```

Ver `DESPLIEGUE.md` para la configuración de la base de datos y el despliegue.

## Flujo de trabajo con Git

1. Actualizar `main` (`git switch main` y `git pull origin main`).
2. Crear una rama `feature/<modulo>`.
3. Hacer commits con mensajes descriptivos (`feat:`, `fix:`, `docs:`, `test:`).
4. Subir la rama y abrir un Pull Request hacia `main`.
5. Otro integrante revisa y aprueba; recién entonces se fusiona.

No se hacen commits directos a `main`.
