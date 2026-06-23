package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

/** Tabla de hechos del CRA por alumno y periodo, con su posición de ranking. */
@Entity
@Table(name = "hecho_cra")
public class HechoCRA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "codigo_alumno")
    private DimAlumno alumno;

    @ManyToOne @JoinColumn(name = "id_facultad")
    private DimFacultad facultad;

    @ManyToOne @JoinColumn(name = "id_escuela")
    private DimEscuelaProfesional escuela;

    @ManyToOne @JoinColumn(name = "id_tiempo")
    private DimTiempo tiempo;

    private double cra;

    @Column(name = "posicion_ranking")
    private Integer posicionRanking;

    public HechoCRA() { }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DimAlumno getAlumno() { return alumno; }
    public void setAlumno(DimAlumno alumno) { this.alumno = alumno; }
    public DimFacultad getFacultad() { return facultad; }
    public void setFacultad(DimFacultad facultad) { this.facultad = facultad; }
    public DimEscuelaProfesional getEscuela() { return escuela; }
    public void setEscuela(DimEscuelaProfesional escuela) { this.escuela = escuela; }
    public DimTiempo getTiempo() { return tiempo; }
    public void setTiempo(DimTiempo tiempo) { this.tiempo = tiempo; }
    public double getCra() { return cra; }
    public void setCra(double cra) { this.cra = cra; }
    public Integer getPosicionRanking() { return posicionRanking; }
    public void setPosicionRanking(Integer posicionRanking) { this.posicionRanking = posicionRanking; }
}
