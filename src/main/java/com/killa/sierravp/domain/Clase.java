package com.killa.sierravp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Una sección/clase concreta de un curso, dictada por un profesor en un periodo.
 * Se añade {@code periodo} (no estaba en V2): permite poblar correctamente la
 * dimensión Tiempo del DataWareHouse.
 */
@Entity
@Table(name = "clase")
public class Clase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    /** Formato "2026-I" / "2026-II". */
    @Column(nullable = false)
    private String periodo;

    @ManyToMany(mappedBy = "clases")
    private Set<Alumno> alumnos = new HashSet<>();

    public Clase() { }
    public Clase(Curso curso, Profesor profesor, String periodo) {
        this.curso = curso;
        this.profesor = profesor;
        this.periodo = periodo;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Profesor getProfesor() { return profesor; }
    public void setProfesor(Profesor profesor) { this.profesor = profesor; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Set<Alumno> getAlumnos() { return alumnos; }
    public void setAlumnos(Set<Alumno> alumnos) { this.alumnos = alumnos; }
}
