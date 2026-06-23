package com.killa.sierravp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** Conoce el periodo académico vigente (configurable en application.properties). */
@Component
public class CalendarioAcademico {

    private final String periodoActual;

    public CalendarioAcademico(@Value("${sierravp.periodo-actual:2026-I}") String periodoActual) {
        this.periodoActual = periodoActual;
    }

    public String getPeriodoActual() {
        return periodoActual;
    }

    /** true si el periodo dado es el vigente (editable); false si está cerrado. */
    public boolean esVigente(String periodo) {
        return periodoActual.equals(periodo);
    }
}
