package com.killa.sierravp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "facultad")
public class Facultad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String nombre;

    @OneToMany(mappedBy = "facultad")
    private Set<EscuelaProfesional> escuelas = new HashSet<>();

    public Facultad() { }
    public Facultad(String nombre) { this.nombre = nombre; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Set<EscuelaProfesional> getEscuelas() { return escuelas; }
    public void setEscuelas(Set<EscuelaProfesional> escuelas) { this.escuelas = escuelas; }
}
