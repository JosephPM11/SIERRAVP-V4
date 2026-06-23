package com.killa.sierravp.service;

import com.killa.sierravp.domain.*;
import com.killa.sierravp.repository.*;
import com.killa.sierravp.util.TipoNota;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Poblado de datos de demostración en la BD transaccional.
 *
 * Crea una facultad, escuelas, cursos, profesores, alumnos, clases (en dos
 * periodos para que la dimensión Tiempo del DW tenga sentido) y notas
 * aleatorias. Es la fuente que luego consume el ETL.
 *
 * Credenciales de demo (todas con contraseña conocida):
 *   admin    -> admin@unmsm.edu.pe        / Admin2026
 *   profesor -> (ver correo generado)     / profe123
 *   alumno   -> (ver correo generado)     / alumno123
 */
@Service
public class SeedService {

    private final FacultadRepository facultadRepo;
    private final EscuelaProfesionalRepository epRepo;
    private final CursoRepository cursoRepo;
    private final ClaseRepository claseRepo;
    private final AlumnoRepository alumnoRepo;
    private final ProfesorRepository profesorRepo;
    private final UsuarioRepository usuarioRepo;
    private final NotaRepository notaRepo;
    private final PasswordEncoder encoder;

    private static final String[] PERIODOS = {"2025-II", "2026-I"};
    /** Año del periodo más reciente; se usa para derivar el ciclo actual. */
    private static final int ANIO_ACTUAL = 2026;
    /** Años de ingreso posibles (el código del alumno empieza con su año de ingreso). */
    private static final int[] ANIOS_INGRESO = {2023, 2024, 2025, 2026};
    private static final String[] NOMBRES = {"Ana", "Luis", "María", "Carlos", "Sofía",
            "Jorge", "Lucía", "Diego", "Valeria", "Andrés", "Camila", "Mateo"};
    private static final String[] APELLIDOS = {"García", "Rojas", "Torres", "Flores", "Díaz",
            "Castro", "Vargas", "Ramos", "Mendoza", "Quispe", "Chávez", "Ríos"};
    private static final String[][] CURSOS = {
            {"Arquitectura de Software", "4"}, {"Base de Datos", "4"},
            {"Estructuras de Datos", "4"}, {"Algoritmia", "3"},
            {"Redes de Computadoras", "3"}, {"Sistemas Operativos", "4"},
            {"Ingeniería de Requisitos", "3"}, {"Programación Web", "3"},
            {"Inteligencia Artificial", "4"}, {"Cálculo III", "5"}};

    public SeedService(FacultadRepository facultadRepo, EscuelaProfesionalRepository epRepo,
                       CursoRepository cursoRepo, ClaseRepository claseRepo,
                       AlumnoRepository alumnoRepo, ProfesorRepository profesorRepo,
                       UsuarioRepository usuarioRepo, NotaRepository notaRepo,
                       PasswordEncoder encoder) {
        this.facultadRepo = facultadRepo;
        this.epRepo = epRepo;
        this.cursoRepo = cursoRepo;
        this.claseRepo = claseRepo;
        this.alumnoRepo = alumnoRepo;
        this.profesorRepo = profesorRepo;
        this.usuarioRepo = usuarioRepo;
        this.notaRepo = notaRepo;
        this.encoder = encoder;
    }

    public boolean yaPoblado() {
        return alumnoRepo.count() > 0;
    }

    @Transactional("txTransactionManager")
    public String poblar() {
        if (yaPoblado()) {
            return "La BD ya tiene datos. (Para regenerar, vacíe las tablas y vuelva a poblar.)";
        }
        Random rnd = new Random(2026);
        // (El admin se crea en el arranque vía DataInitializer.)

        // Facultad + escuelas
        Facultad fac = facultadRepo.save(new Facultad("Ingeniería de Sistemas e Informática"));
        EscuelaProfesional epSoft = epRepo.save(new EscuelaProfesional("Ingeniería de Software", fac));
        EscuelaProfesional epSis = epRepo.save(new EscuelaProfesional("Ingeniería de Sistemas", fac));
        List<EscuelaProfesional> escuelas = List.of(epSoft, epSis);

        // Cursos
        List<Curso> cursos = new ArrayList<>();
        for (String[] c : CURSOS) {
            cursos.add(cursoRepo.save(new Curso(c[0], Integer.parseInt(c[1]))));
        }

        // Profesores
        List<Profesor> profesores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Profesor p = new Profesor();
            p.setEps(new HashSet<>(escuelas));
            crearUsuario(p, 2000 + i,
                    NOMBRES[i % NOMBRES.length], APELLIDOS[(i + 3) % APELLIDOS.length],
                    "profe" + (2000 + i) + "@unmsm.edu.pe", "profe123");
            profesores.add(p);
        }

        // Alumnos: el código empieza con el AÑO DE INGRESO (p. ej. 2024xxxx),
        // y el ciclo se deriva de ese año, de modo que sea coherente con sus periodos.
        List<Alumno> alumnos = new ArrayList<>();
        Map<Integer, Integer> seqPorAnio = new HashMap<>();
        for (int i = 0; i < 60; i++) {
            int anioIngreso = ANIOS_INGRESO[i % ANIOS_INGRESO.length];
            int correlativo = seqPorAnio.merge(anioIngreso, 1, Integer::sum);
            int codigo = anioIngreso * 10000 + correlativo; // ej. 2026 -> 20260001

            Alumno a = new Alumno();
            a.setFacultad(fac);
            a.setEp(escuelas.get(i % 2));
            // Ciclo actual = semestres transcurridos desde el ingreso hasta ANIO_ACTUAL-I.
            a.setCiclo((ANIO_ACTUAL - anioIngreso) * 2 + 1);
            crearUsuario(a, codigo,
                    NOMBRES[i % NOMBRES.length], APELLIDOS[i % APELLIDOS.length],
                    "alumno" + codigo + "@unmsm.edu.pe", "alumno123");
            alumnos.add(a);
        }

        // Clases (en dos periodos) + matrícula + notas
        int totalNotas = 0;
        for (String periodo : PERIODOS) {
            for (int ci = 0; ci < cursos.size(); ci++) {
                Curso curso = cursos.get(ci);
                Profesor prof = profesores.get(ci % profesores.size());
                Clase clase = claseRepo.save(new Clase(curso, prof, periodo));
                int anioPeriodo = Integer.parseInt(periodo.split("-")[0]);

                // Matricular ~30 alumnos, pero solo si ya habían ingresado en ese periodo
                // (un alumno no puede cursar un periodo anterior a su año de ingreso).
                List<Alumno> inscritos = new ArrayList<>();
                for (Alumno a : alumnos) {
                    int anioIngreso = a.getCodigo() / 10000;
                    if (anioPeriodo < anioIngreso) continue;
                    if (rnd.nextDouble() < 0.5) {
                        a.getClases().add(clase);
                        inscritos.add(a);
                    }
                }
                // Notas EC/EP/EF
                for (Alumno a : inscritos) {
                    for (TipoNota tipo : TipoNota.values()) {
                        int cal = 6 + rnd.nextInt(15); // 6..20
                        notaRepo.save(new Nota(a, curso, clase, tipo, cal));
                        totalNotas++;
                    }
                }
            }
        }
        // Persistir la matrícula (lado dueño = Alumno)
        alumnoRepo.saveAll(alumnos);

        return String.format(
                "Poblado OK: 1 facultad, %d escuelas, %d cursos, %d profesores, %d alumnos, %d notas en periodos %s.",
                escuelas.size(), cursos.size(), profesores.size(), alumnos.size(),
                totalNotas, Arrays.toString(PERIODOS));
    }

    private void crearUsuario(Usuario u, int codigo, String nombre, String apellido,
                              String correo, String passwordPlano) {
        u.setCodigo(codigo);
        u.setPrimerNombre(nombre);
        u.setSegundoNombre("");
        u.setPrimerApellido(apellido);
        u.setSegundoApellido("");
        u.setCorreo(correo);
        u.setPassword(encoder.encode(passwordPlano));
        usuarioRepo.save(u);
    }
}
