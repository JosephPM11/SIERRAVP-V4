package com.killa.sierravp.repository;

import com.killa.sierravp.domain.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByCodigo(int codigo);
}
