package com.killa.sierravp.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import com.killa.sierravp.ws.model.GetCraRequest;
import com.killa.sierravp.ws.model.GetCraResponse;

/**
 * CAPA DE INTEGRACIÓN · Web Service SOAP (Spring-WS, contract-first).
 *
 * Publica el endpoint SOAP de consulta de CRA en /ws/* y su WSDL en
 * /ws/cra.wsdl (contrato definido en xsd/cra.xsd). Listo para probar con
 * SoapUI apuntando a http://localhost:8080/ws/cra.wsdl
 */
@EnableWs
@Configuration
public class WebServiceConfig {

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean<>(servlet, "/ws/*");
    }

    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(GetCraRequest.class, GetCraResponse.class);
        return marshaller;
    }

    @Bean
    public XsdSchema craSchema() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/cra.xsd"));
    }

    @Bean(name = "cra")
    public DefaultWsdl11Definition craWsdl(XsdSchema craSchema) {
        DefaultWsdl11Definition definition = new DefaultWsdl11Definition();
        definition.setPortTypeName("CraPort");
        definition.setLocationUri("/ws");
        definition.setTargetNamespace("http://killa.com/sierravp/ws/cra");
        definition.setSchema(craSchema);
        return definition;
    }
}
