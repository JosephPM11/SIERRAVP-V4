package com.killa.sierravp.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/** Alumno (rol ALUMNO): cursa clases, recibe notas, CRA y posición de ranking. */
@Entity
@DiscriminatorValue("alumno")
public class Alumno extends Usuario {

    @ManyToOne
    @JoinColumn(name = "facultad_id")
    private Facultad facultad;

    @ManyToOne
    @JoinColumn(name = "ep_id")
    private EscuelaProfesional ep;

    private int ciclo;

    @ManyToMany
    @JoinTable(name = "alumno_clase",
            joinColumns = @JoinColumn(name = "alumno_id"),
            inverseJoinColumns = @JoinColumn(name = "clase_id"))
    private Set<Clase> clases = new HashSet<>();

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Nota> notas = new HashSet<>();

    @OneToMany(mappedBy = "alumno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CRA> craHistorico = new HashSet<>();

    /** CRA acumulado más reciente (promedio de los CRA por clase del periodo). */
    @Column(name = "cra_ponderado_actual")
    private double craPonderadoActual;

    @Column(name = "posicion_ranking")
    private int posicionRanking;

    public Alumno() { super(); }

    @Override
    public String getRol() { return "alumno"; }

    public Facultad getFacultad() { return facultad; }
    public void setFacultad(Facultad facultad) { this.facultad = facultad; }

    public EscuelaProfesional getEp() { return ep; }
    public void setEp(EscuelaProfesional ep) { this.ep = ep; }

    public int getCiclo() { return ciclo; }
    public void setCiclo(int ciclo) { this.ciclo = ciclo; }

    public Set<Clase> getClases() { return clases; }
    public void setClases(Set<Clase> clases) { this.clases = clases; }

    public Set<Nota> getNotas() { return notas; }
    public void setNotas(Set<Nota> notas) { this.notas = notas; }

    public Set<CRA> getCraHistorico() { return craHistorico; }
    public void setCraHistorico(Set<CRA> craHistorico) { this.craHistorico = craHistorico; }

    public double getCraPonderadoActual() { return craPonderadoActual; }
    public void setCraPonderadoActual(double craPonderadoActual) { this.craPonderadoActual = craPonderadoActual; }

    public int getPosicionRanking() { return posicionRanking; }
    public void setPosicionRanking(int posicionRanking) { this.posicionRanking = posicionRanking; }
}
