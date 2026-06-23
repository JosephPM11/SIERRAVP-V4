package com.killa.sierravp.dw.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "dim_tiempo")
public class DimTiempo {

    @Id
    @Column(name = "id_tiempo")
    private Integer idTiempo;

    private String periodo;
    private int anio;
    private String semestre;

    public DimTiempo() { }
    public DimTiempo(Integer idTiempo, String periodo, int anio, String semestre) {
        this.idTiempo = idTiempo;
        this.periodo = periodo;
        this.anio = anio;
        this.semestre = semestre;
    }

    public Integer getIdTiempo() { return idTiempo; }
    public void setIdTiempo(Integer idTiempo) { this.idTiempo = idTiempo; }
    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }
    public String getSemestre() { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }
}
