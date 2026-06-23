package com.killa.sierravp.service;

import com.killa.sierravp.domain.*;
import com.killa.sierravp.repository.*;
import com.killa.sierravp.util.TipoNota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * UC-05 — Cálculo del CRA NORMALIZADO (la ventaja diferencial del sistema).
 *
 * Para cada CLASE (salón) se mide la distancia del alumno respecto al promedio
 * de su salón:
 *
 *     ponderado = EC*0.3 + EP*0.3 + EF*0.4
 *     cra_clase = 10 + 5 * (ponderado - mediaSalon) / desviacionSalon   (z-score, acotado a [0,20])
 *
 * El CRA del alumno EN EL PERIODO es el PROMEDIO de sus cra_clase de ese periodo.
 * Se guarda UNA fila de CRA por (alumno, periodo) — no una por clase.
 */
@Service
public class CraService {

    private final NotaRepository notaRepo;
    private final CraRepository craRepo;
    private final ClaseRepository claseRepo;
    private final AlumnoRepository alumnoRepo;
    private final CalendarioAcademico calendario;

    public CraService(NotaRepository notaRepo, CraRepository craRepo,
                      ClaseRepository claseRepo, AlumnoRepository alumnoRepo,
                      CalendarioAcademico calendario) {
        this.notaRepo = notaRepo;
        this.craRepo = craRepo;
        this.claseRepo = claseRepo;
        this.alumnoRepo = alumnoRepo;
        this.calendario = calendario;
    }

    /**
     * Recalcula el CRA de todos los alumnos para un periodo (idempotente):
     * una sola fila de CRA por alumno = promedio de sus clases normalizadas.
     */
    @Transactional("txTransactionManager")
    public int calcularTodas(String periodo) {
        craRepo.deleteByPeriodo(periodo);

        Map<Integer, double[]> acumulado = new HashMap<>(); // codigo -> [suma cra_clase, nClases]
        Map<Integer, Alumno> alumnos = new HashMap<>();

        for (Clase c : claseRepo.findAll()) {
            if (!periodo.equals(c.getPeriodo())) continue;   // solo clases de ESTE periodo
            Map<Integer, Double> craClase = craPorAlumnoEnClase(c.getId(), alumnos);
            for (var e : craClase.entrySet()) {
                double[] a = acumulado.computeIfAbsent(e.getKey(), k -> new double[2]);
                a[0] += e.getValue();
                a[1] += 1;
            }
        }

        boolean esPeriodoActual = calendario.esVigente(periodo);
        int alumnosProcesados = 0;
        for (var e : acumulado.entrySet()) {
            double[] a = e.getValue();
            double craPeriodo = a[1] > 0 ? Math.round(a[0] / a[1] * 100.0) / 100.0 : 0.0;
            Alumno al = alumnos.get(e.getKey());

            craRepo.save(new CRA(al, periodo, craPeriodo));

            // El CRA "actual" del alumno (para el ranking vigente) es el del periodo en curso.
            if (esPeriodoActual) {
                al.setCraPonderadoActual(craPeriodo);
                alumnoRepo.save(al);
            }
            alumnosProcesados++;
        }
        return alumnosProcesados;
    }

    /** CRA normalizado de cada alumno dentro de una clase (no persiste nada). */
    private Map<Integer, Double> craPorAlumnoEnClase(int idClase, Map<Integer, Alumno> alumnosOut) {
        List<Nota> notas = notaRepo.findByClaseId(idClase);
        if (notas.isEmpty()) return Map.of();

        Map<Integer, EnumMap<TipoNota, Integer>> porAlumno = new HashMap<>();
        Map<Integer, Alumno> alumnos = new HashMap<>();
        for (Nota n : notas) {
            if (n.getAlumno() == null) continue;
            int cod = n.getAlumno().getCodigo();
            alumnos.putIfAbsent(cod, n.getAlumno());
            porAlumno.computeIfAbsent(cod, k -> new EnumMap<>(TipoNota.class))
                     .put(n.getTipo(), n.getCalificacion());
        }
        if (porAlumno.isEmpty()) return Map.of();

        Map<Integer, Double> ponderados = new HashMap<>();
        for (var e : porAlumno.entrySet()) {
            var m = e.getValue();
            int ec = m.getOrDefault(TipoNota.EC, 0);
            int ep = m.getOrDefault(TipoNota.EP, 0);
            int ef = m.getOrDefault(TipoNota.EF, 0);
            ponderados.put(e.getKey(), ec * 0.3 + ep * 0.3 + ef * 0.4);
        }

        double mean = ponderados.values().stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double var = ponderados.values().stream()
                .mapToDouble(p -> (p - mean) * (p - mean)).average().orElse(0);
        double stddev = Math.sqrt(var);

        Map<Integer, Double> resultado = new HashMap<>();
        for (var e : ponderados.entrySet()) {
            double pond = e.getValue();
            double cra = (stddev == 0) ? mean : (10.0 + 5.0 * (pond - mean) / stddev);
            cra = Math.max(0.0, Math.min(20.0, Math.round(cra * 100.0) / 100.0));
            resultado.put(e.getKey(), cra);
            alumnosOut.putIfAbsent(e.getKey(), alumnos.get(e.getKey()));
        }
        return resultado;
    }
}
