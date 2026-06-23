package com.killa.sierravp.service;

import com.killa.sierravp.domain.*;
import com.killa.sierravp.repository.*;
import com.killa.sierravp.util.TipoNota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/** UC-02 — el profesor consulta sus clases e ingresa/actualiza notas. */
@Service
public class ProfesorService {

    private final ClaseRepository claseRepo;
    private final NotaRepository notaRepo;
    private final AlumnoRepository alumnoRepo;
    private final CalendarioAcademico calendario;

    public ProfesorService(ClaseRepository claseRepo, NotaRepository notaRepo,
                           AlumnoRepository alumnoRepo, CalendarioAcademico calendario) {
        this.claseRepo = claseRepo;
        this.notaRepo = notaRepo;
        this.alumnoRepo = alumnoRepo;
        this.calendario = calendario;
    }

    /** Resumen de clase para el listado. 'editable' = pertenece al periodo vigente. */
    public record ClaseResumen(int id, String curso, String periodo, int numAlumnos, boolean editable) { }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public List<ClaseResumen> resumenClasesDe(int codigoProfesor) {
        return claseRepo.findByProfesorCodigo(codigoProfesor).stream()
                .map(c -> new ClaseResumen(c.getId(), c.getCurso().getNombre(),
                        c.getPeriodo(), c.getAlumnos().size(), calendario.esVigente(c.getPeriodo())))
                // periodo más reciente primero
                .sorted(Comparator.comparing(ClaseResumen::periodo).reversed()
                        .thenComparing(ClaseResumen::curso))
                .toList();
    }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public Clase clase(int idClase) {
        return claseRepo.findById(idClase).orElse(null);
    }

    public boolean esEditable(Clase clase) {
        return clase != null && calendario.esVigente(clase.getPeriodo());
    }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public List<Alumno> alumnosDeClase(int idClase) {
        Clase c = claseRepo.findById(idClase).orElse(null);
        if (c == null) return List.of();
        return c.getAlumnos().stream()
                .sorted(Comparator.comparingInt(Alumno::getCodigo))
                .toList();
    }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public List<Nota> notasDeClase(int idClase) {
        return notaRepo.findByClaseId(idClase);
    }

    /** Inserta o actualiza la nota de un alumno. Solo en el periodo vigente. */
    @Transactional("txTransactionManager")
    public void registrarNota(int idClase, int codigoAlumno, TipoNota tipo, int calificacion) {
        Clase clase = claseRepo.findById(idClase)
                .orElseThrow(() -> new IllegalArgumentException("Clase no existe: " + idClase));

        if (!calendario.esVigente(clase.getPeriodo())) {
            throw new IllegalStateException(
                    "El periodo " + clase.getPeriodo() + " está cerrado; no se pueden modificar sus notas.");
        }

        Alumno alumno = alumnoRepo.findByCodigo(codigoAlumno)
                .orElseThrow(() -> new IllegalArgumentException("Alumno no existe: " + codigoAlumno));

        int cal = Math.max(0, Math.min(20, calificacion));
        Nota nota = notaRepo.findByAlumnoCodigoAndClaseIdAndTipo(codigoAlumno, idClase, tipo)
                .orElseGet(() -> new Nota(alumno, clase.getCurso(), clase, tipo, cal));
        nota.setCalificacion(cal);
        notaRepo.save(nota);
    }
}
