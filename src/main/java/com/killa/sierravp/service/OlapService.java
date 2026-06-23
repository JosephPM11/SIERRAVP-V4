package com.killa.sierravp.service;

import com.killa.sierravp.dw.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Reportes OLAP sobre el DataWareHouse — equivalente web de
 * {@code CreateCrossTab.EXE} / {@code ViewCrossTab.EXE}.
 */
@Service
@Transactional(value = "dwTransactionManager", readOnly = true)
public class OlapService {

    private final HechoNotaRepository hechoNotaRepo;
    private final HechoCraRepository hechoCraRepo;
    private final DimCursoRepository dimCursoRepo;

    public OlapService(HechoNotaRepository hechoNotaRepo, HechoCraRepository hechoCraRepo,
                       DimCursoRepository dimCursoRepo) {
        this.hechoNotaRepo = hechoNotaRepo;
        this.hechoCraRepo = hechoCraRepo;
        this.dimCursoRepo = dimCursoRepo;
    }

    /** REPORTE 1 — promedio de nota final por Facultad x Periodo (cross-tab). */
    public List<Object[]> promedioFacultadPeriodo() {
        return hechoNotaRepo.promedioFacultadPeriodo();
    }

    /** REPORTE 2 — Top-N alumnos por escuela. */
    public List<Object[]> topNPorEscuela(int n) {
        return hechoCraRepo.topNPorEscuela(n);
    }

    /** REPORTE 3 — histórico de CRA de un alumno. */
    public List<Object[]> historicoCra(int codigo) {
        return hechoCraRepo.historicoCra(codigo);
    }

    /** REPORTE 4 — estadísticas de un curso (count/avg/min/max de nota final). */
    public Object[] estadisticasCurso(int idCurso) {
        return (Object[]) hechoNotaRepo.estadisticasCurso(idCurso);
    }

    /** REPORTE 5 — distribución de notas por tipo de evaluación. */
    public List<Object[]> distribucionPorTipo() {
        return hechoNotaRepo.distribucionPorTipo();
    }

    /** Catálogo de cursos disponibles en el DW (para el selector del reporte 4). */
    public List<com.killa.sierravp.dw.domain.DimCurso> cursosDisponibles() {
        return dimCursoRepo.findAll();
    }

    /** Conteos rápidos para el panel de Analítica (diagnóstico del DW). */
    public long totalHechoNota() { return hechoNotaRepo.count(); }
    public long totalHechoCra() { return hechoCraRepo.count(); }
}
