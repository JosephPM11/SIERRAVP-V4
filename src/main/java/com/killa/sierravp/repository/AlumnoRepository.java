package com.killa.sierravp.repository;

import com.killa.sierravp.domain.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByCodigo(int codigo);

    /** Alumnos con facultad/escuela asignada, ordenados para generar el ranking. */
    @Query("SELECT a FROM Alumno a "
         + "WHERE a.facultad IS NOT NULL AND a.ep IS NOT NULL "
         + "ORDER BY a.ep.id ASC, a.craPonderadoActual DESC")
    List<Alumno> findParaRanking();
}
