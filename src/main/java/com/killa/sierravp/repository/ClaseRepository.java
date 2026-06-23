package com.killa.sierravp.repository;

import com.killa.sierravp.domain.Clase;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaseRepository extends JpaRepository<Clase, Integer> {
    List<Clase> findByProfesorCodigo(int codigo);
}
