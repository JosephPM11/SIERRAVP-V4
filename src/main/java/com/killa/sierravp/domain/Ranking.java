package com.killa.sierravp.domain;

import jakarta.persistence.*;

/**
 * Posición de un alumno dentro de su Escuela Profesional, ordenada por CRA
 * (que ya incorpora la normalización respecto al promedio del salón).
 */
@Entity
@Table(name = "ranking")
public class Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @ManyToOne
    @JoinColumn(name = "ep_id")
    private EscuelaProfesional escuelaProfesional;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    @Column(nullable = false)
    private int posicion;

    public Ranking() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public EscuelaProfesional getEscuelaProfesional() { return escuelaProfesional; }
    public void setEscuelaProfesional(EscuelaProfesional escuelaProfesional) { this.escuelaProfesional = escuelaProfesional; }

    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad facultad) { this.facultad = facultad; }

    public int getPosicion() { return posicion; }
    public void setPosicion(int posicion) { this.posicion = posicion; }
}
