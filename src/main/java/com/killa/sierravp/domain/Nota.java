package com.killa.sierravp.domain;

import com.killa.sierravp.util.TipoNota;
import jakarta.persistence.*;

@Entity
@Table(name = "nota")
public class Nota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "alumno_id")
    private Alumno alumno;

    @ManyToOne(optional = false)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(optional = false)
    @JoinColumn(name = "clase_id")
    private Clase clase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNota tipo;

    @Column(nullable = false)
    private int calificacion;

    public Nota() { }
    public Nota(Alumno alumno, Curso curso, Clase clase, TipoNota tipo, int calificacion) {
        this.alumno = alumno;
        this.curso = curso;
        this.clase = clase;
        this.tipo = tipo;
        this.calificacion = calificacion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Alumno getAlumno() { return alumno; }
    public void setAlumno(Alumno alumno) { this.alumno = alumno; }

    public Curso getCurso() { return curso; }
    public void setCurso(Curso curso) { this.curso = curso; }

    public Clase getClase() { return clase; }
    public void setClase(Clase clase) { this.clase = clase; }

    public TipoNota getTipo() { return tipo; }
    public void setTipo(TipoNota tipo) { this.tipo = tipo; }

    public int getCalificacion() { return calificacion; }
    public void setCalificacion(int calificacion) { this.calificacion = calificacion; }
}
