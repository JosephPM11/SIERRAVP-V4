package com.killa.sierravp.repository;

import com.killa.sierravp.domain.CRA;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CraRepository extends JpaRepository<CRA, Long> {
    List<CRA> findByAlumnoCodigoOrderByPeriodoAsc(int codigo);
    List<CRA> findByPeriodo(String periodo);
    void deleteByPeriodo(String periodo);
}
