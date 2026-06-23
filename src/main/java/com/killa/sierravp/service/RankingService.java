package com.killa.sierravp.service;

import com.killa.sierravp.domain.Alumno;
import com.killa.sierravp.domain.Ranking;
import com.killa.sierravp.repository.AlumnoRepository;
import com.killa.sierravp.repository.RankingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UC-06 — Genera el ranking de alumnos por Escuela Profesional ordenando por el
 * CRA acumulado (que ya incorpora la normalización respecto al promedio del
 * salón). Persiste la tabla Ranking y la posición en cada alumno.
 */
@Service
public class RankingService {

    private final AlumnoRepository alumnoRepo;
    private final RankingRepository rankingRepo;

    public RankingService(AlumnoRepository alumnoRepo, RankingRepository rankingRepo) {
        this.alumnoRepo = alumnoRepo;
        this.rankingRepo = rankingRepo;
    }

    @Transactional("txTransactionManager")
    public int generarRanking() {
        rankingRepo.deleteAllInBatch();

        List<Alumno> alumnos = alumnoRepo.findParaRanking();
        Integer epActual = null;
        int posicion = 0;
        int filas = 0;

        for (Alumno a : alumnos) {
            int epId = a.getEp().getId();
            if (epActual == null || epActual != epId) {
                epActual = epId;
                posicion = 1;
            } else {
                posicion++;
            }
            Ranking r = new Ranking();
            r.setAlumno(a);
            r.setEscuelaProfesional(a.getEp());
            r.setFacultad(a.getFacultad());
            r.setPosicion(posicion);
            rankingRepo.save(r);

            a.setPosicionRanking(posicion);
            alumnoRepo.save(a);
            filas++;
        }
        return filas;
    }
}
