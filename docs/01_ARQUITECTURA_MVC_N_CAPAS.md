# 01 · Arquitectura Web MVC con N Capas

Documento de arquitectura del 3er entregable de SIERRAVP.

## 1. Patrón base: MVC

La aplicación sigue el patrón **Modelo–Vista–Controlador**, materializado con Spring Boot:

| Componente MVC | Implementación | Paquete |
|----------------|----------------|---------|
| **Modelo**      | Entidades JPA + servicios de negocio | `domain`, `dw.domain`, `service` |
| **Vista**       | Plantillas Thymeleaf (HTML/CSS/JS) — FRONT-END | `resources/templates`, `resources/static` |
| **Controlador** | Controladores Spring MVC — BACK-END | `web` |

Esto responde a las tres modalidades de desarrollo que menciona el documento del curso:
- **FRONT-END (JS/CSS/HTML)** → Thymeleaf + `app.css`.
- **BACK-END (JSP/ASP/PHP/…)** → controladores Spring MVC (equivalente moderno de JSP).
- **Lenguaje del DBMS** → SQL/JPQL ejecutado vía JPA/Hibernate sobre MySQL.

## 2. Las N capas

```
1. CAPA DE APLICACIÓN  → Spring MVC (controllers) + Thymeleaf (vistas) + servicios.
2. CAPA DE DATOS       → MySQL transaccional (OLTP)  = "Servidor de Datos".
3. CAPA DATAWAREHOUSE  → MySQL dimensional (OLAP)    = "Servidor de DataWareHouse".
4. CAPA FTP            → buzón de archivos para transferencia (simulada con carpeta).
5. CAPA MIRROR         → réplica/espejo de los datos (simulada con carpeta).
```

### 2.1 Capa de Aplicación
Proceso único Spring Boot. El navegador es el **cliente ligero**: solo procesa HTML/CSS/JS;
toda la lógica reside en el servidor de aplicaciones. Flujo de una petición:

```
Navegador → Controller (web) → Service (service) → Repository (repository) → MySQL
```

### 2.2 Capa de Datos (transaccional)
- Esquema `sierravp_transaccional`.
- Entidades normalizadas: `Usuario` (alumno/profesor/admin), `Facultad`, `EscuelaProfesional`,
  `Curso`, `Clase`, `Nota`, `CRA`, `Ranking`.
- Configurada en `config/TransaccionalDataSourceConfig` (datasource **primario**).
- El **código del alumno empieza con su año de ingreso** (`AÑO*10000 + correlativo`,
  p. ej. `20260001`) y su `ciclo` se deriva de ese año. Por eso un alumno nunca tiene
  notas en periodos anteriores a su ingreso (lo controla `SeedService` al poblar).
- `config/DataInitializer` crea el usuario administrador al arrancar (si no existe), de
  modo que se pueda iniciar sesión y "Cargar datos de demostración" desde el primer uso.

### 2.3 Capa DataWareHouse (dimensional)
- Esquema `sierravp_datawarehouse`, modelo en **estrella**.
- Dimensiones: `DimTiempo`, `DimFacultad`, `DimEscuelaProfesional`, `DimCurso`, `DimAlumno`.
- Hechos: `HechoNota`, `HechoCRA`.
- Configurada en `config/DataWareHouseDataSourceConfig` (segundo datasource).
- Se alimenta con el **ETL** (`EtlService` = `GenerarDatawareHouse`), estrategia *truncate + reload*.
- Se explota con **OLAP** (`OlapService` = `CreateCrossTab` / `ViewCrossTab`).

> En la demo ambos esquemas viven en el mismo MySQL; en producción serían dos
> servidores físicos. El código ya los trata como dos unidades de persistencia
> independientes (dos `EntityManagerFactory`, dos `TransactionManager`), así que
> separarlos es solo cambiar `sierravp.dw.host` en `application.properties`.

### 2.4 Capa FTP y Capa Mirror (simuladas)
Ver [03_FTP_Mirror_simulado.md](03_FTP_Mirror_simulado.md). Implementadas en
`FtpMirrorService` + scripts en `scripts/`.

## 3. La ventaja diferencial: CRA normalizado

El ranking **no** usa la nota cruda 0–20, sino la **distancia del alumno respecto al
promedio de su salón** (estandarización tipo z-score reescalada):

```
ponderado  = EC*0.3 + EP*0.3 + EF*0.4
cra_clase  = 10 + 5 * (ponderado − mediaSalón) / desviaciónSalón    (acotado a [0,20])
CRA(periodo) = promedio de los cra_clase del alumno en ese periodo
```

Implementado en `service/CraService`: se guarda **una fila de CRA por (alumno, periodo)**
(no una por clase). El `craPonderadoActual` del alumno = CRA del **periodo vigente**, y el
ranking (`service/RankingService`) ordena por ese valor dentro de cada Escuela Profesional.

`service/CalendarioAcademico` conoce el periodo vigente (`sierravp.periodo-actual`): solo
ese periodo admite ingreso/edición de notas; los anteriores quedan **cerrados** (solo lectura).

## 4. Seguridad (capa transversal)

Spring Security con login por formulario y autorización por rol
(`ROLE_ALUMNO`, `ROLE_PROFESOR`, `ROLE_ADMIN`). Contraseñas cifradas con BCrypt.
Reglas en `config/SecurityConfig`:

| Ruta | Rol exigido |
|------|-------------|
| `/profesor/**`  | PROFESOR |
| `/alumno/**`    | ALUMNO |
| `/sistema/**`, `/analitica/**` | ADMIN |

## 5. Tecnologías

- Java 21, Spring Boot 3.3 (Web, Data JPA, Security, Thymeleaf, Validation).
- Hibernate 6 / JPA (Jakarta).
- MySQL 8 (driver `mysql-connector-j`).
- Thymeleaf + extras Spring Security 6.
- Maven (empaquetado).
