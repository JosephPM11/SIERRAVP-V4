package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_curso")
public class DimCurso {

    @Id
    @Column(name = "id_curso")
    private Integer idCurso;

    private String nombre;

    /** Ahora sí con valor real (la V2 lo dejaba en null). */
    private Integer creditos;

    public DimCurso() { }
    public DimCurso(Integer idCurso, String nombre, Integer creditos) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.creditos = creditos;
    }

    public Integer getIdCurso() { return idCurso; }
    public void setIdCurso(Integer idCurso) { this.idCurso = idCurso; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Integer getCreditos() { return creditos; }
    public void setCreditos(Integer creditos) { this.creditos = creditos; }
}
