package com.killa.sierravp.web;

import com.killa.sierravp.domain.Usuario;
import com.killa.sierravp.service.ConsultaService;
import com.killa.sierravp.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/alumno")
public class AlumnoController {

    private final ConsultaService consultaService;
    private final CurrentUserService currentUser;

    public AlumnoController(ConsultaService consultaService, CurrentUserService currentUser) {
        this.consultaService = consultaService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String miRendimiento(Principal principal, Model model) {
        Usuario u = currentUser.actual(principal);
        model.addAttribute("usuario", u);
        model.addAttribute("rendimiento", consultaService.consultar(u.getCodigo()));
        return "alumno/rendimiento";
    }
}
