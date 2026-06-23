package com.killa.sierravp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de SIERRAVP-V3 (3er entregable).
 *
 * Arquitectura Web MVC con N capas:
 *   - Capa de Aplicación  : Spring MVC + Thymeleaf (este proceso).
 *   - Capa de Datos       : MySQL transaccional  (Servidor de Datos).
 *   - Capa DataWareHouse  : MySQL dimensional     (Servidor de DataWareHouse).
 *   - Capa FTP / Mirror   : simuladas con carpetas locales (ver docs/).
 *
 * Se ejecuta con:  mvn spring-boot:run     (o)   java -jar target/sierravp-web.jar
 * y se abre en:    http://localhost:8080
 */
@SpringBootApplication
public class SierravpWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SierravpWebApplication.class, args);
    }
}
