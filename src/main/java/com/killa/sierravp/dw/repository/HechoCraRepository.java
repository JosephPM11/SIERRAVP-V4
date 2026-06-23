package com.killa.sierravp.dw.repository;

import com.killa.sierravp.dw.domain.HechoCRA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface HechoCraRepository extends JpaRepository<HechoCRA, Long> {

    /** Filas ordenadas por (escuela, periodo, cra desc) para asignar posición. */
    @Query("SELECT h FROM HechoCRA h "
         + "ORDER BY h.escuela.idEscuela, h.tiempo.idTiempo, h.cra DESC")
    List<HechoCRA> findOrdenadoParaRanking();

    /** REPORTE 2 — Top-N alumnos por escuela (ranking). */
    @Query("SELECT h.escuela.nombre, h.alumno.nombreCompleto, h.cra, h.posicionRanking "
         + "FROM HechoCRA h "
         + "WHERE h.posicionRanking IS NOT NULL AND h.posicionRanking <= :n "
         + "ORDER BY h.escuela.nombre, h.posicionRanking")
    List<Object[]> topNPorEscuela(@Param("n") int n);

    /** REPORTE 3 — histórico de CRA de un alumno. */
    @Query("SELECT h.tiempo.periodo, h.cra, h.posicionRanking, h.escuela.nombre "
         + "FROM HechoCRA h WHERE h.alumno.codigoAlumno = :cod "
         + "ORDER BY h.tiempo.anio, h.tiempo.semestre")
    List<Object[]> historicoCra(@Param("cod") int codigo);
}
