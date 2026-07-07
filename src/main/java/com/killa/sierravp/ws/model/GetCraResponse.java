package com.killa.sierravp.ws.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Payload de salida del web service SOAP de consulta de CRA. */
@XmlRootElement(name = "getCraResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCraResponse {

    private int codigoAlumno;
    private boolean encontrado;
    private String nombre;
    private Double cra;
    private Integer posicionRanking;
    private String mensaje;

    public GetCraResponse() { }

    public int getCodigoAlumno() { return codigoAlumno; }
    public void setCodigoAlumno(int codigoAlumno) { this.codigoAlumno = codigoAlumno; }

    public boolean isEncontrado() { return encontrado; }
    public void setEncontrado(boolean encontrado) { this.encontrado = encontrado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Double getCra() { return cra; }
    public void setCra(Double cra) { this.cra = cra; }

    public Integer getPosicionRanking() { return posicionRanking; }
    public void setPosicionRanking(Integer posicionRanking) { this.posicionRanking = posicionRanking; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
}
