package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

/**
 * Dimensión Alumno. Usa el código del alumno como clave natural y denormaliza
 * facultad y escuela como FKs para permitir drill-down sin más joins.
 */
@Entity
@Table(name = "dim_alumno")
public class DimAlumno {

    @Id
    @Column(name = "codigo_alumno")
    private Integer codigoAlumno;

    private String nombreCompleto;
    private String correo;
    private int ciclo;

    @ManyToOne
    @JoinColumn(name = "id_facultad")
    private DimFacultad facultad;

    @ManyToOne
    @JoinColumn(name = "id_escuela")
    private DimEscuelaProfesional escuela;

    public DimAlumno() { }

    public Integer getCodigoAlumno() { return codigoAlumno; }
    public void setCodigoAlumno(Integer codigoAlumno) { this.codigoAlumno = codigoAlumno; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public int getCiclo() { return ciclo; }
    public void setCiclo(int ciclo) { this.ciclo = ciclo; }
    public DimFacultad getFacultad() { return facultad; }
    public void setFacultad(DimFacultad facultad) { this.facultad = facultad; }
    public DimEscuelaProfesional getEscuela() { return escuela; }
    public void setEscuela(DimEscuelaProfesional escuela) { this.escuela = escuela; }
}
