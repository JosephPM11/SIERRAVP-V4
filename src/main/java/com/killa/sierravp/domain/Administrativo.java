package com.killa.sierravp.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** Usuario administrativo (rol ADMIN): opera los procesos de Sistema y Analítica. */
@Entity
@DiscriminatorValue("admin")
public class Administrativo extends Usuario {

    public Administrativo() { super(); }

    @Override
    public String getRol() { return "admin"; }
}
