package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_facultad")
public class DimFacultad {

    @Id
    @Column(name = "id_facultad")
    private Integer idFacultad;

    private String nombre;

    public DimFacultad() { }
    public DimFacultad(Integer idFacultad, String nombre) {
        this.idFacultad = idFacultad;
        this.nombre = nombre;
    }

    public Integer getIdFacultad() { return idFacultad; }
    public void setIdFacultad(Integer idFacultad) { this.idFacultad = idFacultad; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
