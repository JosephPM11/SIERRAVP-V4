package com.killa.sierravp.ws;

import com.killa.sierravp.service.ConsultaService;
import com.killa.sierravp.ws.model.GetCraRequest;
import com.killa.sierravp.ws.model.GetCraResponse;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Web service SOAP (contract-first, Spring-WS) para consultar el CRA de un
 * alumno: código, nombre y posición en el ranking. Reutiliza {@link ConsultaService},
 * el mismo servicio que usa la vista "Mi rendimiento" del alumno.
 *
 * WSDL publicado en: /ws/cra.wsdl
 */
@Endpoint
public class CraEndpoint {

    private static final String NAMESPACE_URI = "http://killa.com/sierravp/ws/cra";

    private final ConsultaService consultaService;

    public CraEndpoint(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getCraRequest")
    @ResponsePayload
    public GetCraResponse getCra(@RequestPayload GetCraRequest request) {
        GetCraResponse response = new GetCraResponse();
        response.setCodigoAlumno(request.getCodigoAlumno());

        ConsultaService.Rendimiento r = consultaService.consultar(request.getCodigoAlumno());
        if (r == null) {
            response.setEncontrado(false);
            response.setMensaje("No existe un alumno con código " + request.getCodigoAlumno());
        } else {
            response.setEncontrado(true);
            response.setNombre(r.nombre());
            response.setCra(r.craPonderado());
            response.setPosicionRanking(r.posicionRanking());
        }
        return response;
    }
}
