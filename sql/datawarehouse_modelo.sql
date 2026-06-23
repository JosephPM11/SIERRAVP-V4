-- ============================================================
--  Modelo dimensional (esquema estrella) del DataWareHouse
--  SIERRAVP-V3.  Referencia/documentación: en la app estas
--  tablas las crea Hibernate (ddl-auto=update) en el esquema
--  sierravp_datawarehouse. Este script sirve para revisión y
--  para crearlas manualmente si se desea.
-- ============================================================

CREATE DATABASE IF NOT EXISTS sierravp_datawarehouse;
USE sierravp_datawarehouse;

-- ---------- DIMENSIONES ----------
CREATE TABLE IF NOT EXISTS dim_tiempo (
    id_tiempo INT PRIMARY KEY,
    periodo   VARCHAR(20),
    anio      INT,
    semestre  VARCHAR(5)
);

CREATE TABLE IF NOT EXISTS dim_facultad (
    id_facultad INT PRIMARY KEY,
    nombre      VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS dim_escuela_profesional (
    id_escuela  INT PRIMARY KEY,
    nombre      VARCHAR(255),
    id_facultad INT,
    FOREIGN KEY (id_facultad) REFERENCES dim_facultad(id_facultad)
);

CREATE TABLE IF NOT EXISTS dim_curso (
    id_curso INT PRIMARY KEY,
    nombre   VARCHAR(255),
    creditos INT
);

CREATE TABLE IF NOT EXISTS dim_alumno (
    codigo_alumno   INT PRIMARY KEY,
    nombre_completo VARCHAR(255),
    correo          VARCHAR(255),
    ciclo           INT,
    id_facultad     INT,
    id_escuela      INT,
    FOREIGN KEY (id_facultad) REFERENCES dim_facultad(id_facultad),
    FOREIGN KEY (id_escuela)  REFERENCES dim_escuela_profesional(id_escuela)
);

-- ---------- HECHOS ----------
CREATE TABLE IF NOT EXISTS hecho_nota (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_alumno   INT,
    id_curso        INT,
    id_facultad     INT,
    id_escuela      INT,
    id_tiempo       INT,
    tipo_evaluacion VARCHAR(5),
    calificacion    INT,
    FOREIGN KEY (codigo_alumno) REFERENCES dim_alumno(codigo_alumno),
    FOREIGN KEY (id_curso)      REFERENCES dim_curso(id_curso),
    FOREIGN KEY (id_facultad)   REFERENCES dim_facultad(id_facultad),
    FOREIGN KEY (id_escuela)    REFERENCES dim_escuela_profesional(id_escuela),
    FOREIGN KEY (id_tiempo)     REFERENCES dim_tiempo(id_tiempo)
);

CREATE TABLE IF NOT EXISTS hecho_cra (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_alumno    INT,
    id_facultad      INT,
    id_escuela       INT,
    id_tiempo        INT,
    cra              DOUBLE,
    posicion_ranking INT,
    FOREIGN KEY (codigo_alumno) REFERENCES dim_alumno(codigo_alumno),
    FOREIGN KEY (id_facultad)   REFERENCES dim_facultad(id_facultad),
    FOREIGN KEY (id_escuela)    REFERENCES dim_escuela_profesional(id_escuela),
    FOREIGN KEY (id_tiempo)     REFERENCES dim_tiempo(id_tiempo)
);

-- Índices de apoyo para consultas OLAP
CREATE INDEX idx_hnota_facultad ON hecho_nota(id_facultad);
CREATE INDEX idx_hnota_curso    ON hecho_nota(id_curso);
CREATE INDEX idx_hcra_escuela   ON hecho_cra(id_escuela);
