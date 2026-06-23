package com.killa.sierravp.service;

import com.killa.sierravp.domain.Alumno;
import com.killa.sierravp.domain.CRA;
import com.killa.sierravp.domain.Nota;
import com.killa.sierravp.repository.AlumnoRepository;
import com.killa.sierravp.repository.CraRepository;
import com.killa.sierravp.repository.NotaRepository;
import com.killa.sierravp.util.TipoNota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/** UC-03/04 — consulta del rendimiento académico e histórico de notas de un alumno. */
@Service
public class ConsultaService {

    private final AlumnoRepository alumnoRepo;
    private final NotaRepository notaRepo;
    private final CraRepository craRepo;

    public ConsultaService(AlumnoRepository alumnoRepo, NotaRepository notaRepo, CraRepository craRepo) {
        this.alumnoRepo = alumnoRepo;
        this.notaRepo = notaRepo;
        this.craRepo = craRepo;
    }

    /** Notas de un curso en un periodo (EC/EP/EF + ponderado). */
    public record NotaCurso(String curso, Integer ec, Integer ep, Integer ef, Double ponderado) { }

    /** Un periodo académico con su CRA y el detalle por curso. */
    public record PeriodoAcademico(String periodo, Double cra, List<NotaCurso> cursos) { }

    /** Vista materializada del rendimiento (evita lazy-loading en la plantilla). */
    public record Rendimiento(
            int codigo, String nombre, String escuela, String facultad,
            double craPonderado, int posicionRanking,
            List<PeriodoAcademico> periodos) { }

    @Transactional(value = "txTransactionManager", readOnly = true)
    public Rendimiento consultar(int codigo) {
        Alumno a = alumnoRepo.findByCodigo(codigo).orElse(null);
        if (a == null) return null;

        // periodo -> curso -> notas por tipo
        Map<String, Map<String, EnumMap<TipoNota, Integer>>> porPeriodo =
                new TreeMap<>(Comparator.reverseOrder());
        for (Nota n : notaRepo.findByAlumnoCodigo(codigo)) {
            String per = n.getClase() != null ? n.getClase().getPeriodo() : "(sin periodo)";
            String cur = n.getCurso() != null ? n.getCurso().getNombre() : "(sin curso)";
            porPeriodo.computeIfAbsent(per, k -> new TreeMap<>())
                      .computeIfAbsent(cur, k -> new EnumMap<>(TipoNota.class))
                      .put(n.getTipo(), n.getCalificacion());
        }

        // CRA promedio por periodo
        Map<String, double[]> craAcum = new HashMap<>();
        for (CRA c : craRepo.findByAlumnoCodigoOrderByPeriodoAsc(codigo)) {
            double[] x = craAcum.computeIfAbsent(c.getPeriodo(), k -> new double[2]);
            x[0] += c.getCra();
            x[1] += 1;
        }

        List<PeriodoAcademico> periodos = new ArrayList<>();
        for (var pe : porPeriodo.entrySet()) {
            List<NotaCurso> cursos = new ArrayList<>();
            for (var cu : pe.getValue().entrySet()) {
                EnumMap<TipoNota, Integer> m = cu.getValue();
                Integer ec = m.get(TipoNota.EC), ep = m.get(TipoNota.EP), ef = m.get(TipoNota.EF);
                Double pond = (ec != null && ep != null && ef != null)
                        ? Math.round((ec * 0.3 + ep * 0.3 + ef * 0.4) * 100.0) / 100.0 : null;
                cursos.add(new NotaCurso(cu.getKey(), ec, ep, ef, pond));
            }
            double[] cr = craAcum.get(pe.getKey());
            Double cra = (cr != null && cr[1] > 0) ? Math.round(cr[0] / cr[1] * 100.0) / 100.0 : null;
            periodos.add(new PeriodoAcademico(pe.getKey(), cra, cursos));
        }

        return new Rendimiento(
                a.getCodigo(),
                a.nombreCompleto(),
                a.getEp() != null ? a.getEp().getNombre() : "-",
                a.getFacultad() != null ? a.getFacultad().getNombre() : "-",
                a.getCraPonderadoActual(),
                a.getPosicionRanking(),
                periodos);
    }
}
