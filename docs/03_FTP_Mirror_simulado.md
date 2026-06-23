# 03 · Capas FTP y Mirror (simuladas)

El 3er entregable lista, además de Aplicación / Datos / DataWareHouse, dos capas más:
**FTP** y **Mirror**. Aquí se representan de forma **simulada** con carpetas locales,
suficiente para la demo y para ilustrar el concepto, sin montar un servidor FTP ni una
réplica MySQL real.

## 1. Concepto

| Capa | Rol real | Simulación en V3 |
|------|----------|------------------|
| **FTP** | Servidor de transferencia de archivos: la aplicación publica exports/backups que otros sistemas consumen. | Carpeta `sierravp.ftp.dir` (por defecto `~/SIERRAVP_FTP`) donde se escribe un CSV con el ranking. |
| **Mirror** | Réplica/espejo de la base de datos (alta disponibilidad / backup). | Carpeta `sierravp.mirror.dir` (por defecto `~/SIERRAVP_MIRROR`) que recibe una copia de lo publicado en FTP. |

## 2. Cómo se usa desde la app

En **Analítica** (rol admin):
- **Publicar en FTP** → `FtpMirrorService.publicarEnFtp()` genera
  `ranking_<timestamp>.csv` en la carpeta FTP a partir de la tabla `Ranking`.
- **Replicar a Mirror** → `FtpMirrorService.replicarEnMirror()` copia todos los
  archivos del buzón FTP a la carpeta espejo.

Las rutas configurables están en `application.properties`:
```
sierravp.ftp.dir=${user.home}/SIERRAVP_FTP
sierravp.mirror.dir=${user.home}/SIERRAVP_MIRROR
```

## 3. Alternativa a nivel de Sistema Operativo

Para demostrar la réplica fuera de la aplicación (como haría un job programado),
se incluyen scripts en `scripts/`:

- **Windows**: `scripts/mirror_sync.bat`
- **Linux/Mac**: `scripts/mirror_sync.sh`

Ambos copian el contenido de la carpeta FTP a la carpeta Mirror. Pueden agendarse con
el Programador de tareas de Windows o `cron`.

## 4. Cómo escalar a algo "real" (notas para sustentación)

- **FTP real**: levantar un servidor FTP (FileZilla Server / vsftpd) y reemplazar la
  escritura en carpeta por un cliente FTP (Apache Commons Net). El resto del flujo no cambia.
- **Mirror real**: configurar replicación maestro–esclavo de MySQL (binlog) entre el
  Servidor de Datos y un servidor espejo; el "Mirror" dejaría de ser una carpeta y pasaría
  a ser una BD réplica de solo lectura.
