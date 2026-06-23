package com.killa.sierravp.repository;

import com.killa.sierravp.domain.Nota;
import com.killa.sierravp.util.TipoNota;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotaRepository extends JpaRepository<Nota, Long> {
    List<Nota> findByClaseId(int claseId);
    List<Nota> findByAlumnoCodigo(int codigo);
    Optional<Nota> findByAlumnoCodigoAndClaseIdAndTipo(int codigo, int claseId, TipoNota tipo);
}
