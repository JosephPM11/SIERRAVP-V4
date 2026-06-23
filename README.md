# SIERRAVP — Versión 3 (3er Entregable)

**Sistema Integral de Evaluación del Rendimiento Académico — Aplicación Web MVC con N capas.**

Proyecto académico (UNMSM — FISI). Esta versión adapta SIERRAVP al **3er entregable**
del curso: *Arquitectura MVC con N Capas (Aplicación, Datos, FTP, Mirror, DataWareHouse)*,
implementada como **aplicación web** (el punto que faltaba aclarar en clase).

Reutiliza la lógica de negocio de la V2 (CRA normalizado, ranking, ETL y OLAP) y la
expone sobre **Spring Boot MVC + Thymeleaf**, con **dos servidores de datos** (transaccional
y datawarehouse) y las capas **FTP** y **Mirror** simuladas.

---

## 1. Arquitectura (resumen)

```
   Navegador (cliente ligero)
        │  HTTP
        ▼
 ┌──────────────────────────────┐
 │  CAPA DE APLICACIÓN (MVC)     │   Spring Boot + Thymeleaf
 │  Controller → Service → Repo  │   (Modelo · Vista · Controlador)
 └───────┬───────────────┬───────┘
         │ JDBC          │ JDBC
         ▼               ▼
 ┌───────────────┐  ┌───────────────────────┐
 │ SERVIDOR DE   │  │ SERVIDOR DE            │
 │ DATOS (OLTP)  │  │ DATAWAREHOUSE (OLAP)   │
 │ MySQL         │  │ MySQL (dimensional)    │
 └───────┬───────┘  └───────────────────────┘
         │ export
         ▼
 ┌───────────────┐      replica      ┌───────────────┐
 │ CAPA FTP      │  ───────────────► │ CAPA MIRROR   │
 │ (carpeta)     │                   │ (carpeta)     │
 └───────────────┘                   └───────────────┘
```

Detalle completo en [docs/01_ARQUITECTURA_MVC_N_CAPAS.md](docs/01_ARQUITECTURA_MVC_N_CAPAS.md).

Casos de uso cubiertos: **CU-01** (login + roles), **CU-02** (ingreso de notas),
**CU-03/04** (consulta de rendimiento), **CU-05** (CRA normalizado), **CU-06** (ranking),
**ETL + OLAP** (datawarehouse) y **FTP/Mirror** (simuladas).

---

## 2. Requisitos

- **Java 21** (ya instalado en `C:\Program Files\Java\jdk-21`).
- **Maven 3.9+** — si no lo tienes en PATH, usa el Maven que trae IntelliJ
  (`Run` sobre `SierravpWebApplication`) o instálalo.
- **MySQL 8** corriendo en `localhost:3306`.

> Las dos bases de datos (`sierravp_transaccional` y `sierravp_datawarehouse`) se
> **crean solas** en el primer arranque (`createDatabaseIfNotExist=true` + Hibernate
> `ddl-auto=update`). Solo necesitas que el servidor MySQL esté encendido.

Ajusta usuario/clave de MySQL en [`src/main/resources/application.properties`](src/main/resources/application.properties)
(`sierravp.datos.user`, `sierravp.datos.pass`, etc.). Por defecto usa `root` / `root`.

---

## 3. Ejecutar

```powershell
# Opción A: Maven en PATH
mvn spring-boot:run

# Opción B: empaquetar y correr el JAR
mvn clean package
java -jar target/sierravp-web.jar

# Opción C: IntelliJ → abrir el pom.xml como proyecto → Run 'SierravpWebApplication'
```

Abre **http://localhost:8080** → te redirige al login.

---

## 4. Recorrido de la demo (5 min)

En el menú superior, las secciones de admin aparecen como **Gestión académica** y **Reportes**.

1. **Login como admin**: `admin@unmsm.edu.pe` / `Admin2026` (el admin se crea solo al arrancar).
2. **Gestión académica → Cargar datos de demostración** (genera facultad, escuelas, cursos,
   profesores, 60 alumnos con años de ingreso 2023–2026, clases en 2 periodos y notas).
3. **Gestión académica → Calcular CRA** para `2025-II` y luego `2026-I` (CU-05, normalización).
4. **Gestión académica → Generar ranking** (CU-06).
5. **Reportes → Ejecutar ETL** (carga el datawarehouse).
6. **Reportes → Ver reportes OLAP** (cross-tabs, Top-N, histórico por periodo, etc.).
7. **Reportes → Publicar en FTP** y **Replicar a Mirror** (capas simuladas).
8. **Cerrar sesión** y entrar como **profesor** (`profe2000@unmsm.edu.pe` / `profe123`)
   para ingresar notas (solo del periodo vigente), o como **alumno**
   (`alumno20260001@unmsm.edu.pe` / `alumno123`) para ver su CRA, ranking e histórico.

> El **código del alumno empieza con su año de ingreso** (p. ej. `2023xxxx`, `2026xxxx`)
> y su ciclo se deriva de ese año. Solo se pueden ingresar/editar notas del **periodo
> vigente** (`sierravp.periodo-actual`); los periodos anteriores quedan en solo lectura.

Guion detallado en [docs/02_DESPLIEGUE.md](docs/02_DESPLIEGUE.md).

---

## 5. Estructura

```
SIERRAVP-V3/
├── pom.xml
├── src/main/java/com/killa/sierravp/
│   ├── SierravpWebApplication.java
│   ├── config/        # 2 datasources, seguridad, admin inicial (DataInitializer)
│   ├── domain/        # entidades OLTP
│   ├── dw/domain/     # entidades dimensionales (DW)
│   ├── repository/    # Spring Data (transaccional)
│   ├── dw/repository/ # Spring Data (datawarehouse)
│   ├── service/       # CRA, ranking, ETL, OLAP, seed, FTP/Mirror, calendario, seguridad
│   └── web/           # controladores MVC
├── src/main/resources/
│   ├── application.properties
│   ├── templates/     # vistas Thymeleaf
│   └── static/css/    # estilos
├── sql/               # DDL de referencia del DW
├── scripts/           # mirror_sync (FTP→Mirror a nivel SO)
└── docs/              # documentación de arquitectura y despliegue
```

---

## 6. Qué mejora respecto a V2

- **Es web (MVC)**, requisito del 3er entregable, en vez de consola/Swing, con una
  **interfaz institucional** (estilo UNMSM) sin elementos técnicos en las vistas de usuario.
- **CRA por periodo**: se guarda **una fila de CRA por (alumno, periodo)** = promedio de
  sus clases normalizadas, y el ranking usa el CRA del periodo vigente (la V2 se quedaba
  con la última clase).
- **Dimensión Tiempo real**: `Clase` tiene `periodo`, así el DW no colapsa todo a un solo tiempo.
- **`Curso.creditos`** poblado (en V2 quedaba `null` en el DW).
- **Código del alumno = año de ingreso + correlativo**, con ciclo coherente; un alumno
  nunca aparece en periodos anteriores a su ingreso.
- **Periodos cerrados**: solo se editan notas del periodo vigente; los anteriores son de
  solo lectura (`CalendarioAcademico`).
- **Histórico de notas por periodo** para el alumno (y por periodo en los reportes OLAP).
- **Seguridad**: credenciales fuera del binario, contraseñas **BCrypt**, login y roles.
- Añade **capas FTP y Mirror** simuladas, que el 3er entregable lista explícitamente.
