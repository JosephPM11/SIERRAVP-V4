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

1. **Login como admin**: `admin@unmsm.edu.pe` / `Admin2026`.
2. **Sistema → Poblar BD** (genera facultad, escuelas, cursos, profesores, ~60 alumnos,
   clases en 2 periodos y notas).
3. **Sistema → Calcular CRA** para `2026-I` y luego `2025-II` (CU-05, normalización).
4. **Sistema → Generar ranking** (CU-06).
5. **Analítica → Ejecutar ETL** (carga el datawarehouse).
6. **Analítica → Ver reportes OLAP** (cross-tabs, Top-N, histórico, etc.).
7. **Analítica → Publicar en FTP** y **Replicar a Mirror** (capas simuladas).
8. **Cerrar sesión** y entrar como **profesor** (`profe2000@unmsm.edu.pe` / `profe123`)
   para ingresar notas, o como **alumno** (`alumno20260001@unmsm.edu.pe` / `alumno123`)
   para ver su CRA y posición.

Guion detallado en [docs/02_DESPLIEGUE.md](docs/02_DESPLIEGUE.md).

---

## 5. Estructura

```
SIERRAVP-V3/
├── pom.xml
├── src/main/java/com/killa/sierravp/
│   ├── SierravpWebApplication.java
│   ├── config/        # 2 datasources (tx + DW) + seguridad
│   ├── domain/        # entidades OLTP
│   ├── dw/domain/     # entidades dimensionales (DW)
│   ├── repository/    # Spring Data (transaccional)
│   ├── dw/repository/ # Spring Data (datawarehouse)
│   ├── service/       # CRA, ranking, ETL, OLAP, seed, FTP/Mirror, seguridad
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

- **Es web (MVC)**, requisito del 3er entregable, en vez de consola/Swing.
- **Corrige el ranking acumulado**: el `craPonderadoActual` es el promedio de los CRA
  por clase del periodo, no el de la última clase procesada.
- **Dimensión Tiempo real**: `Clase` ahora tiene `periodo`, así el DW deja de colapsar
  todo a un único tiempo.
- **`Curso.creditos`** poblado (en V2 quedaba `null` en el DW).
- **Credenciales fuera del binario** y **contraseñas cifradas** (BCrypt) en vez de texto plano.
- Añade **capas FTP y Mirror** simuladas, que el 3er entregable lista explícitamente.
