package com.killa.sierravp.service;

import com.killa.sierravp.domain.*;
import com.killa.sierravp.dw.domain.*;
import com.killa.sierravp.dw.repository.*;
import com.killa.sierravp.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Servicio ETL — equivalente web del {@code GenerarDatawareHouse.EXE}.
 *
 * Extrae de la BD transaccional (Servidor de Datos) y carga el modelo
 * dimensional (Servidor de DataWareHouse) con estrategia TRUNCATE + RELOAD
 * (idempotente). Corre dentro de la transacción del DataWareHouse.
 */
@Service
public class EtlService {

    // Origen (transaccional)
    private final FacultadRepository facultadRepo;
    private final EscuelaProfesionalRepository epRepo;
    private final CursoRepository cursoRepo;
    private final AlumnoRepository alumnoRepo;
    private final ClaseRepository claseRepo;
    private final NotaRepository notaRepo;
    private final CraRepository craRepo;

    // Destino (datawarehouse)
    private final DimFacultadRepository dimFacRepo;
    private final DimEscuelaProfesionalRepository dimEscRepo;
    private final DimCursoRepository dimCurRepo;
    private final DimAlumnoRepository dimAluRepo;
    private final DimTiempoRepository dimTiempoRepo;
    private final HechoNotaRepository hechoNotaRepo;
    private final HechoCraRepository hechoCraRepo;

    public EtlService(FacultadRepository facultadRepo, EscuelaProfesionalRepository epRepo,
                      CursoRepository cursoRepo, AlumnoRepository alumnoRepo,
                      ClaseRepository claseRepo, NotaRepository notaRepo, CraRepository craRepo,
                      DimFacultadRepository dimFacRepo, DimEscuelaProfesionalRepository dimEscRepo,
                      DimCursoRepository dimCurRepo, DimAlumnoRepository dimAluRepo,
                      DimTiempoRepository dimTiempoRepo, HechoNotaRepository hechoNotaRepo,
                      HechoCraRepository hechoCraRepo) {
        this.facultadRepo = facultadRepo;
        this.epRepo = epRepo;
        this.cursoRepo = cursoRepo;
        this.alumnoRepo = alumnoRepo;
        this.claseRepo = claseRepo;
        this.notaRepo = notaRepo;
        this.craRepo = craRepo;
        this.dimFacRepo = dimFacRepo;
        this.dimEscRepo = dimEscRepo;
        this.dimCurRepo = dimCurRepo;
        this.dimAluRepo = dimAluRepo;
        this.dimTiempoRepo = dimTiempoRepo;
        this.hechoNotaRepo = hechoNotaRepo;
        this.hechoCraRepo = hechoCraRepo;
    }

    @Transactional("dwTransactionManager")
    public LinkedHashMap<String, Object> ejecutar() {
        long t0 = System.currentTimeMillis();
        LinkedHashMap<String, Object> r = new LinkedHashMap<>();

        // 1. Limpieza (primero hechos, luego dimensiones por las FK)
        hechoNotaRepo.deleteAllInBatch();
        hechoCraRepo.deleteAllInBatch();
        dimAluRepo.deleteAllInBatch();
        dimEscRepo.deleteAllInBatch();
        dimFacRepo.deleteAllInBatch();
        dimCurRepo.deleteAllInBatch();
        dimTiempoRepo.deleteAllInBatch();

        // 2. Dimensiones
        Map<Integer, DimFacultad> facMap = new HashMap<>();
        for (Facultad f : facultadRepo.findAll()) {
            facMap.put(f.getId(), dimFacRepo.save(new DimFacultad(f.getId(), f.getNombre())));
        }

        Map<Integer, DimEscuelaProfesional> escMap = new HashMap<>();
        for (EscuelaProfesional ep : epRepo.findAll()) {
            DimFacultad fac = ep.getFacultad() != null ? facMap.get(ep.getFacultad().getId()) : null;
            escMap.put(ep.getId(),
                    dimEscRepo.save(new DimEscuelaProfesional(ep.getId(), ep.getNombre(), fac)));
        }

        Map<Integer, DimCurso> curMap = new HashMap<>();
        for (Curso c : cursoRepo.findAll()) {
            curMap.put(c.getId(), dimCurRepo.save(new DimCurso(c.getId(), c.getNombre(), c.getCreditos())));
        }

        Map<Integer, DimAlumno> aluMap = new HashMap<>();
        for (Alumno a : alumnoRepo.findAll()) {
            if (a.getFacultad() == null || a.getEp() == null) continue;
            DimAlumno d = new DimAlumno();
            d.setCodigoAlumno(a.getCodigo());
            d.setNombreCompleto(a.nombreCompleto());
            d.setCorreo(a.getCorreo());
            d.setCiclo(a.getCiclo());
            d.setFacultad(facMap.get(a.getFacultad().getId()));
            d.setEscuela(escMap.get(a.getEp().getId()));
            aluMap.put(a.getCodigo(), dimAluRepo.save(d));
        }

        // Dimensión Tiempo: periodos reales de las clases (corrige V2)
        Set<String> periodos = new TreeSet<>();
        for (Clase c : claseRepo.findAll()) {
            if (c.getPeriodo() != null) periodos.add(c.getPeriodo());
        }
        if (periodos.isEmpty()) periodos.add(periodoActual());
        Map<String, DimTiempo> tiempoMap = new HashMap<>();
        int i = 1;
        for (String p : periodos) {
            tiempoMap.put(p, dimTiempoRepo.save(
                    new DimTiempo(i, p, parseAnio(p), parseSemestre(p))));
            i++;
        }

        r.put("dim_facultad", facMap.size());
        r.put("dim_escuela_profesional", escMap.size());
        r.put("dim_curso", curMap.size());
        r.put("dim_alumno", aluMap.size());
        r.put("dim_tiempo", tiempoMap.size());

        // 3. Hechos
        DimTiempo tDefault = tiempoMap.values().iterator().next();

        int notasCargadas = 0;
        for (Nota n : notaRepo.findAll()) {
            Alumno a = n.getAlumno();
            if (a == null || a.getFacultad() == null || a.getEp() == null) continue;
            DimAlumno alu = aluMap.get(a.getCodigo());
            DimCurso cur = curMap.get(n.getCurso().getId());
            if (alu == null || cur == null) continue;
            DimTiempo t = (n.getClase() != null && tiempoMap.containsKey(n.getClase().getPeriodo()))
                    ? tiempoMap.get(n.getClase().getPeriodo()) : tDefault;

            HechoNota h = new HechoNota();
            h.setAlumno(alu);
            h.setCurso(cur);
            h.setFacultad(facMap.get(a.getFacultad().getId()));
            h.setEscuela(escMap.get(a.getEp().getId()));
            h.setTiempo(t);
            h.setTipoEvaluacion(n.getTipo() != null ? n.getTipo().name() : "EF");
            h.setCalificacion(n.getCalificacion());
            hechoNotaRepo.save(h);
            notasCargadas++;
        }

        int crasCargados = 0;
        for (CRA c : craRepo.findAll()) {
            Alumno a = c.getAlumno();
            if (a == null || a.getFacultad() == null || a.getEp() == null) continue;
            DimAlumno alu = aluMap.get(a.getCodigo());
            if (alu == null) continue;
            DimTiempo t = tiempoMap.getOrDefault(c.getPeriodo(), tDefault);

            HechoCRA h = new HechoCRA();
            h.setAlumno(alu);
            h.setFacultad(facMap.get(a.getFacultad().getId()));
            h.setEscuela(escMap.get(a.getEp().getId()));
            h.setTiempo(t);
            h.setCra(c.getCra());
            hechoCraRepo.save(h);
            crasCargados++;
        }

        calcularRankings();

        r.put("hecho_nota", notasCargadas);
        r.put("hecho_cra", crasCargados);
        r.put("segundos", (System.currentTimeMillis() - t0) / 1000.0);
        return r;
    }

    /** Asigna posición de ranking dentro de cada (escuela, periodo) en el DW. */
    private void calcularRankings() {
        List<HechoCRA> filas = hechoCraRepo.findOrdenadoParaRanking();
        Integer epActual = null, tActual = null;
        int posicion = 0;
        for (HechoCRA h : filas) {
            Integer ep = h.getEscuela() != null ? h.getEscuela().getIdEscuela() : null;
            Integer t = h.getTiempo() != null ? h.getTiempo().getIdTiempo() : null;
            if (!Objects.equals(ep, epActual) || !Objects.equals(t, tActual)) {
                epActual = ep;
                tActual = t;
                posicion = 1;
            } else {
                posicion++;
            }
            h.setPosicionRanking(posicion);
            hechoCraRepo.save(h);
        }
    }

    private int parseAnio(String periodo) {
        try { return Integer.parseInt(periodo.split("-")[0]); }
        catch (Exception e) { return Calendar.getInstance().get(Calendar.YEAR); }
    }

    private String parseSemestre(String periodo) {
        try { return periodo.split("-")[1]; }
        catch (Exception e) { return "I"; }
    }

    private String periodoActual() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) < 6 ? "I" : "II");
    }
}
