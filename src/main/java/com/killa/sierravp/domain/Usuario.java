package com.killa.sierravp.domain;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Clase base de todos los usuarios del sistema (estrategia SINGLE_TABLE).
 * El discriminador {@code tipo_usuario} distingue alumno / profesor / admin.
 *
 * El login se hace por {@code correo}; el {@code codigo} es la llave de negocio
 * (carné universitario) usada en consultas de rendimiento.
 */
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_usuario", discriminatorType = DiscriminatorType.STRING)
public abstract class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private int codigo;

    @Column(nullable = false)
    private String primerNombre;

    private String segundoNombre;

    @Column(nullable = false)
    private String primerApellido;

    private String segundoApellido;

    @Column(unique = true, nullable = false)
    private String correo;

    /** Contraseña cifrada con BCrypt. */
    @Column(nullable = false)
    private String password;

    public Usuario() { }

    public String nombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (primerNombre != null) sb.append(primerNombre).append(' ');
        if (segundoNombre != null) sb.append(segundoNombre).append(' ');
        if (primerApellido != null) sb.append(primerApellido).append(' ');
        if (segundoApellido != null) sb.append(segundoApellido);
        return sb.toString().trim();
    }

    /** "alumno" | "profesor" | "admin" — implementado por cada subclase. */
    public abstract String getRol();

    // ---- getters / setters ----
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getCodigo() { return codigo; }
    public void setCodigo(int codigo) { this.codigo = codigo; }

    public String getPrimerNombre() { return primerNombre; }
    public void setPrimerNombre(String primerNombre) { this.primerNombre = primerNombre; }

    public String getSegundoNombre() { return segundoNombre; }
    public void setSegundoNombre(String segundoNombre) { this.segundoNombre = segundoNombre; }

    public String getPrimerApellido() { return primerApellido; }
    public void setPrimerApellido(String primerApellido) { this.primerApellido = primerApellido; }

    public String getSegundoApellido() { return segundoApellido; }
    public void setSegundoApellido(String segundoApellido) { this.segundoApellido = segundoApellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
