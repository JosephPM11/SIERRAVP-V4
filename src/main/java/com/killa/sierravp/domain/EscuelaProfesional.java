package com.killa.sierravp.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "escuela_profesional")
public class EscuelaProfesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    public EscuelaProfesional() { }
    public EscuelaProfesional(String nombre, Facultad facultad) {
        this.nombre = nombre;
        this.facultad = facultad;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad facultad) { this.facultad = facultad; }
}
