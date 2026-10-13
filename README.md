# Sistema de Gestión de Sismos — Sprint 2 (PA2)

Grupo 6 · Desarrollo de Aplicaciones Empresariales Avanzado · ISIL 2026-20

Aplicación web en Java (Servlets, JSP y MVC) con persistencia en **MySQL** mediante **JPA (EclipseLink)**.

## Puesta en marcha (cada integrante, una sola vez)

1. **MySQL 8 o superior** iniciado.
2. En DBeaver (o MySQL Workbench) ejecutar, en este orden:
   - `database/01_schema.sql`: crea la base `cssp_sismos` y sus tablas. **Si ya existían, las borra y las vuelve a crear.**
   - `database/02_datos_iniciales.sql`: carga los datos de ejemplo (12 departamentos, 22 distritos, 10 estaciones y 30 sismos).
3. Crear el archivo `src/main/resources/db-local.properties` con tu usuario y contraseña de MySQL:
   ```properties
   usuario=root
   contrasena=tu_contraseña
   ```
   Este archivo está en `.gitignore`: **nunca se sube al repositorio**. `db.properties` solo tiene los valores por defecto.
4. Recargar Maven (NetBeans: Reload POM · IntelliJ: Reload All Maven Projects) y ejecutar en **Tomcat 10.1 u 11**.
5. Abrir `http://localhost:8080/sistema-de-gestion-de-sismos/sismos`.

Para volver a los datos de ejemplo (por ejemplo, antes de grabar el video), ejecutar otra vez los dos scripts.

## Estado de las HU del sprint 2

| HU | Descripción | Responsable | Estado |
|----|-------------|-------------|--------|
| HU-15 | Buscar, filtrar, ordenar y paginar sismos | José | ✅ |
| HU-09 | Gestionar ubicaciones geográficas | José | ✅ |
| HU-21 | Seleccionar la ubicación en cascada | Víctor | ⏳ |
| HU-10 | Gestionar estaciones de monitoreo | Víctor | ⏳ |
| HU-11 | Asociar un sismo a la estación que lo registró | Cristian | ⏳ |
| HU-30 | Filtrar sismos por estado del evento | Cristian | ⏳ |
| HU-31 | Buscar un sismo por código o referencia geográfica | Nick | ⏳ |

Base del proyecto (José): migración a JPA y MySQL, script SQL, entidades con relaciones y repositorios JPA.

## Modelo de datos

```
Departamento 1 ── N Provincia 1 ── N Distrito 1 ── N Sismo
                                       │                ▲
                                       └── N Estación 1 ┘ (opcional)
Sismo 1 ── N ReporteAfectacion        Sismo 1 ── N SeguimientoSismo
```

Las tablas y columnas de `database/01_schema.sql` coinciden con las anotaciones `@Table` y `@Column`
de las entidades. Las restricciones `UNIQUE`, `CHECK` y `FOREIGN KEY` están en el script.

## Estructura

```
src/main/java/com/grupo6/sistema/
├── config/       JpaUtil: crea el EntityManagerFactory (unidad SismosPU) y lee db.properties
├── model/        Entidades JPA (@Entity) y TipoUbicacion
├── repository/   RepositorioJpa (CRUD con EntityManager y transacciones) y un repositorio por entidad
├── busqueda/     SismoBusqueda (filtros, orden y página) y Pagina
├── servicio/     UbicacionServicio (reglas del catálogo de ubicaciones)
├── validacion/   Formularios y validadores
├── listener/     AplicacionListener: comparte los repositorios y cierra JPA al detener
└── servlet/      Controladores
src/main/resources/
├── META-INF/persistence.xml
└── db.properties (+ db-local.properties, solo en tu computadora)
src/main/webapp/WEB-INF/views/
├── comun/barra.jspf   Barra superior compartida (agregar aquí el enlace de un módulo nuevo)
├── sismos/  ubicaciones/  error/
database/   01_schema.sql y 02_datos_iniciales.sql
```

## Reglas para no romper el código de los demás

- **Repositorios:** tomarlos del `ServletContext` con las constantes de `AplicacionListener`.
- **Transacciones:** todo lo que modifica datos pasa por `agregar`, `actualizar` o `eliminar` de `RepositorioJpa`,
  que ya hacen `begin`, `commit` y `rollback`. Para consultas nuevas usar `consultar(...)`.
- **Relaciones:** si una entidad tiene un `@ManyToOne`, sobrescribir `prepararRelaciones` en su repositorio
  (ver `SismoRepository`) para usar `em.getReference(...)`.
- **Tablas nuevas o columnas nuevas:** agregarlas al script SQL y a la entidad con el mismo nombre.
- **Estados:** usar las constantes de `Sismo` y `EstacionMonitoreo`, nunca escribir los textos a mano.
- **Ramas:** una rama por HU creada desde `main` actualizado y unirla con un Pull Request.

## Guía rápida para las HU pendientes

- **HU-10 Estaciones (Víctor):** la entidad `EstacionMonitoreo` y su repositorio ya existen
  (`listar`, `buscarPorId`, `buscarPorCodigo`, `listarActivas`, `contarSismos`, `agregar`, `actualizar`, `eliminar`).
  Falta el CRUD de pantallas en `/estaciones`, igual que el de sismos, y el enlace en `comun/barra.jspf`.
- **HU-21 Cascada (Víctor):** en `sismos/formulario.jsp` reemplazar la lista de distritos por tres listas
  (departamento → provincia → distrito). `ProvinciaRepository.listarPorDepartamento` y
  `DistritoRepository.listarPorProvincia` ya existen. El formulario debe seguir enviando `distritoId`.
- **HU-11 Sismo y estación (Cristian):** la relación `Sismo N ── 1 Estación` y la regla de estación inactiva
  ya están en la base (`SismoValidador`). Queda completar los criterios de la HU en las pantallas.
- **HU-30 Estado (Cristian) y HU-31 Código o referencia (Nick):** agregar el filtro en `SismoBusqueda`
  (campo, lectura en `desdeRequest` y en `parametrosFiltro`), su condición JPQL en
  `SismoRepository.agregarCondiciones` y el campo en el formulario de filtros de `sismos/lista.jsp`.

## Rutas

| Ruta | Qué hace |
|---|---|
| GET `/sismos` | Lista con filtros (`departamentoId`, `magnitudMin`, `magnitudMax`, `desde`, `hasta`), orden (`orden`, `dir`) y página (`pagina`) |
| GET/POST `/sismos/nuevo`, `/sismos/editar?id=X`, `/sismos/eliminar?id=X` | CRUD de sismos |
| GET `/sismos/detalle?id=X` | Detalle |
| GET `/ubicaciones`, `?departamentoId=X`, `?provinciaId=Y` | Catálogo de ubicaciones por nivel |
| GET/POST `/ubicaciones/nuevo?tipo=…&padreId=…`, `/ubicaciones/editar?tipo=…&id=…`, `/ubicaciones/eliminar?tipo=…&id=…` | CRUD de departamentos, provincias y distritos |

La consola de Tomcat muestra las sentencias SQL que genera JPA (útil para la demostración del video).
