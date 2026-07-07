package com.killa.sierravp.ws.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/** Payload de entrada del web service SOAP de consulta de CRA. */
@XmlRootElement(name = "getCraRequest")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetCraRequest {

    private int codigoAlumno;

    public GetCraRequest() { }

    public int getCodigoAlumno() { return codigoAlumno; }
    public void setCodigoAlumno(int codigoAlumno) { this.codigoAlumno = codigoAlumno; }
}
