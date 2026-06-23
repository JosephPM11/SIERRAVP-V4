package com.killa.sierravp.domain;

import jakarta.persistence.*;

/**
 * CRA (Coeficiente de Rendimiento Académico) normalizado de un alumno en un
 * periodo. Es la nota traducida a la "distancia respecto al promedio del salón"
 * (z-score reescalado a media 10, desviación 5). Ver {@code CraService}.
 */
@Entity
@Table(name = "cra")
public class CRA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @Column(nullable = false)
    private String periodo;

    @Column(nullable = false)
    private double cra;

    public CRA() { }
    public CRA(Alumno alumno, String periodo, double cra) {
        this.alumno = alumno;
        this.periodo = periodo;
        this.cra = cra;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public double getCra() { return cra; }
    public void setCra(double cra) { this.cra = cra; }
}
