package com.killa.sierravp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/** Profesor (rol PROFESOR): dicta clases e ingresa notas. */
@Entity
@DiscriminatorValue("profesor")
public class Profesor extends Usuario {

    @OneToMany(mappedBy = "profesor")
    private Set<Clase> clases = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "profesor_ep",
            joinColumns = @JoinColumn(name = "profesor_id"),
            inverseJoinColumns = @JoinColumn(name = "ep_id"))
    private Set<EscuelaProfesional> eps = new HashSet<>();

    public Profesor() { super(); }

    @Override
    public String getRol() { return "profesor"; }

    public Set<Clase> getClases() { return clases; }
    public void setClases(Set<Clase> clases) { this.clases = clases; }

    public Set<EscuelaProfesional> getEps() { return eps; }
    public void setEps(Set<EscuelaProfesional> eps) { this.eps = eps; }
}
