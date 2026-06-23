package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

/** Tabla de hechos a nivel de nota individual (grano: alumno-curso-tipo-tiempo). */
@Entity
@Table(name = "hecho_nota")
public class HechoNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "codigo_alumno")
    private DimAlumno alumno;

    @ManyToOne @JoinColumn(name = "id_curso")
    private DimCurso curso;

    @ManyToOne @JoinColumn(name = "id_facultad")
    private DimFacultad facultad;

    @ManyToOne @JoinColumn(name = "id_escuela")
    private DimEscuelaProfesional escuela;

    @ManyToOne @JoinColumn(name = "id_tiempo")
    private DimTiempo tiempo;

    @Column(name = "tipo_evaluacion")
    private String tipoEvaluacion;

    private int calificacion;

    public HechoNota() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DimAlumno getAlumno() { return alumno; }
    public void setAlumno(DimAlumno alumno) { this.alumno = alumno; }
    public DimCurso getCurso() { return curso; }
    public void setCurso(DimCurso curso) { this.curso = curso; }
    public DimFacultad getFacultad() { return facultad; }
    public void setFacultad(DimFacultad facultad) { this.facultad = facultad; }
    public DimEscuelaProfesional getEscuela() { return escuela; }
    public void setEscuela(DimEscuelaProfesional escuela) { this.escuela = escuela; }
    public DimTiempo getTiempo() { return tiempo; }
    public void setTiempo(DimTiempo tiempo) { this.tiempo = tiempo; }
    public String getTipoEvaluacion() { return tipoEvaluacion; }
    public void setTipoEvaluacion(String tipoEvaluacion) { this.tipoEvaluacion = tipoEvaluacion; }
    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }
}
