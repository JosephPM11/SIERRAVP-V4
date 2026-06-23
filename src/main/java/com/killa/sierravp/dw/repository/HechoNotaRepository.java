package com.killa.sierravp.dw.repository;

import com.killa.sierravp.dw.domain.HechoNota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HechoNotaRepository extends JpaRepository<HechoNota, Long> {

    /** REPORTE 1 — cross-tab Facultad x Periodo (promedio de nota final). */
    @Query("SELECT h.facultad.nombre, h.tiempo.periodo, AVG(h.calificacion) "
         + "FROM HechoNota h WHERE h.tipoEvaluacion = 'EF' "
         + "GROUP BY h.facultad.nombre, h.tiempo.periodo "
         + "ORDER BY h.facultad.nombre, h.tiempo.periodo")
    List<Object[]> promedioFacultadPeriodo();

    /** REPORTE 4 — estadísticas de un curso (nota final). */
    @Query("SELECT COUNT(h), AVG(h.calificacion), MIN(h.calificacion), MAX(h.calificacion) "
         + "FROM HechoNota h WHERE h.curso.idCurso = :idCurso AND h.tipoEvaluacion = 'EF'")
    Object estadisticasCurso(@Param("idCurso") int idCurso);

    /** REPORTE 5 — distribución de notas por tipo de evaluación. */
    @Query("SELECT h.tipoEvaluacion, COUNT(h), AVG(h.calificacion) "
         + "FROM HechoNota h GROUP BY h.tipoEvaluacion ORDER BY h.tipoEvaluacion")
    List<Object[]> distribucionPorTipo();
}
