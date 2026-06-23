package com.killa.sierravp.service;

import com.killa.sierravp.domain.Alumno;
import com.killa.sierravp.repository.AlumnoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

/**
 * CAPA FTP (simulada) + CAPA MIRROR (simulada).
 *
 * - FTP    : publica un export (CSV) en una carpeta "buzón" ({@code sierravp.ftp.dir}),
 *            tal como un servicio que sube datos a un servidor FTP.
 * - MIRROR : replica el contenido del buzón FTP a una carpeta espejo
 *            ({@code sierravp.mirror.dir}), simulando una BD réplica/backup.
 *
 * En producción estas dos capas serían un servidor FTP real y una réplica
 * maestro-esclavo de MySQL; aquí se representan con carpetas locales para la
 * demo (ver docs/03_FTP_Mirror_simulado.md).
 */
@Service
public class FtpMirrorService {

    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private final AlumnoRepository alumnoRepo;
    private final Path ftpDir;
    private final Path mirrorDir;

    public FtpMirrorService(AlumnoRepository alumnoRepo,
                            @Value("${sierravp.ftp.dir}") String ftpDir,
                            @Value("${sierravp.mirror.dir}") String mirrorDir) {
        this.alumnoRepo = alumnoRepo;
        this.ftpDir = Path.of(ftpDir);
        this.mirrorDir = Path.of(mirrorDir);
    }

    public String ftpPath()    { return ftpDir.toString(); }
    public String mirrorPath() { return mirrorDir.toString(); }

    /** Publica un CSV con el ranking actual en el buzón FTP. */
    @Transactional(value = "txTransactionManager", readOnly = true)
    public String publicarEnFtp() {
        try {
            Files.createDirectories(ftpDir);
            List<Alumno> alumnos = alumnoRepo.findParaRanking();

            StringBuilder sb = new StringBuilder("codigo,nombre,escuela,cra,posicion\n");
            for (Alumno a : alumnos) {
                sb.append(a.getCodigo()).append(',')
                  .append('"').append(a.nombreCompleto()).append('"').append(',')
                  .append('"').append(a.getEp() != null ? a.getEp().getNombre() : "").append('"').append(',')
                  .append(a.getCraPonderadoActual()).append(',')
                  .append(a.getPosicionRanking()).append('\n');
            }
            Path archivo = ftpDir.resolve("ranking_" + LocalDateTime.now().format(TS) + ".csv");
            Files.writeString(archivo, sb.toString());
            return "Publicado en FTP: " + archivo + "  (" + alumnos.size() + " filas)";
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Replica TODO el buzón FTP hacia la carpeta espejo (Mirror). */
    public String replicarEnMirror() {
        try {
            Files.createDirectories(ftpDir);
            Files.createDirectories(mirrorDir);
            int copiados = 0;
            try (Stream<Path> files = Files.list(ftpDir)) {
                for (Path p : (Iterable<Path>) files::iterator) {
                    if (Files.isRegularFile(p)) {
                        Files.copy(p, mirrorDir.resolve(p.getFileName()),
                                StandardCopyOption.REPLACE_EXISTING);
                        copiados++;
                    }
                }
            }
            return "Replicado a Mirror: " + copiados + " archivo(s) en " + mirrorDir;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
