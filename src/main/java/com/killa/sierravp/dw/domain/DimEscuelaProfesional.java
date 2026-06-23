package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_escuela_profesional")
public class DimEscuelaProfesional {

    @Id
    @Column(name = "id_escuela")
    private Integer idEscuela;

    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_facultad")
    private DimFacultad facultad;

    public DimEscuelaProfesional() { }
    public DimEscuelaProfesional(Integer idEscuela, String nombre, DimFacultad facultad) {
        this.idEscuela = idEscuela;
        this.nombre = nombre;
        this.facultad = facultad;
    }

    public Integer getIdEscuela() { return idEscuela; }
    public void setIdEscuela(Integer idEscuela) { this.idEscuela = idEscuela; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public DimFacultad getFacultad() { return facultad; }
    public void setFacultad(DimFacultad facultad) { this.facultad = facultad; }
}
