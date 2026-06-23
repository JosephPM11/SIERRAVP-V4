# 02 · Despliegue y guion de demo

## 1. Preparar MySQL

1. Tener MySQL 8 encendido en `localhost:3306`.
2. (Opcional) crear un usuario dedicado:
   ```sql
   CREATE USER 'sierravp_app'@'%' IDENTIFIED BY 'Sierra2026';
   GRANT ALL PRIVILEGES ON sierravp_transaccional.* TO 'sierravp_app'@'%';
   GRANT ALL PRIVILEGES ON sierravp_datawarehouse.* TO 'sierravp_app'@'%';
   FLUSH PRIVILEGES;
   ```
3. Poner esas credenciales en `src/main/resources/application.properties`
   (`sierravp.datos.user/pass` y `sierravp.dw.user/pass`). Por defecto: `root` / `root`.

No hace falta crear las BD ni las tablas a mano: se generan en el primer arranque.

## 2. Arrancar la aplicación

```powershell
mvn spring-boot:run
```
o, empaquetando:
```powershell
mvn clean package
java -jar target/sierravp-web.jar
```
o desde IntelliJ: abrir el `pom.xml`, esperar la descarga de dependencias y ejecutar
`SierravpWebApplication`.

Verás en consola:
```
[DataSource transaccional] jdbc:mysql://localhost:3306/sierravp_transaccional ... OK
[DataSource datawarehouse] jdbc:mysql://localhost:3306/sierravp_datawarehouse ... OK
Tomcat started on port 8080
```

Abrir **http://localhost:8080**.

## 3. Guion de la demo (8–10 min)

| Paso | Acción | Qué demuestra |
|------|--------|----------------|
| 1 | Login `admin@unmsm.edu.pe` / `Admin2026` | CU-01 (autenticación + rol) |
| 2 | Sistema → **Poblar BD** | Carga del OLTP (datos sintéticos) |
| 3 | Sistema → **Calcular CRA** `2026-I` y `2025-II` | CU-05 (normalización de notas) |
| 4 | Sistema → **Generar ranking** | CU-06 (ranking por escuela) |
| 5 | Analítica → **Ejecutar ETL** | Carga del DataWareHouse (estrella) |
| 6 | Analítica → **Ver reportes OLAP** | Cross-tab, Top-N, histórico, stats |
| 7 | Analítica → **Publicar en FTP** + **Replicar a Mirror** | Capas FTP y Mirror |
| 8 | Logout → login como profesor → ingresar una nota | CU-02 |
| 9 | Logout → login como alumno → ver rendimiento | CU-03/04 |

### Credenciales de demo (tras “Poblar BD”)
- **Admin**: `admin@unmsm.edu.pe` / `Admin2026`
- **Profesor**: `profe2000@unmsm.edu.pe` … `profe2004@unmsm.edu.pe` / `profe123`
- **Alumno**: `alumno20260001@unmsm.edu.pe` … `alumno20260060@unmsm.edu.pe` / `alumno123`

## 4. Problemas comunes

| Síntoma | Causa / solución |
|---------|------------------|
| `Communications link failure` al arrancar | MySQL apagado o host/puerto mal en `application.properties` |
| `Access denied for user` | Ajusta `sierravp.datos.user/pass` y `sierravp.dw.user/pass` |
| Reportes OLAP vacíos | Faltó **Ejecutar ETL** (paso 5), o no calculaste CRA antes |
| `mvn` no reconocido | Usa IntelliJ (Maven embebido) o instala Maven y agrégalo al PATH |
| El alumno ve CRA 0 y posición 0 | Aún no corriste Calcular CRA + Generar ranking |

## 5. Dos servidores reales (opcional)

Para separar físicamente el DataWareHouse, cambia en `application.properties`:
```
sierravp.dw.host=IP_DEL_SEGUNDO_SERVIDOR
```
No se toca código: las dos unidades de persistencia ya están desacopladas.
