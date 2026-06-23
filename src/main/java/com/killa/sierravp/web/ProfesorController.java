package com.killa.sierravp.web;

import com.killa.sierravp.domain.Clase;
import com.killa.sierravp.domain.Usuario;
import com.killa.sierravp.service.CurrentUserService;
import com.killa.sierravp.service.ProfesorService;
import com.killa.sierravp.util.TipoNota;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/profesor")
public class ProfesorController {

    private final ProfesorService profesorService;
    private final CurrentUserService currentUser;

    public ProfesorController(ProfesorService profesorService, CurrentUserService currentUser) {
        this.profesorService = profesorService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String misClases(Principal principal, Model model) {
        Usuario u = currentUser.actual(principal);
        model.addAttribute("usuario", u);
        model.addAttribute("clases", profesorService.resumenClasesDe(u.getCodigo()));
        return "profesor/clases";
    }

    @GetMapping("/clase/{id}")
    public String verClase(@PathVariable int id, Principal principal, Model model) {
        Clase clase = profesorService.clase(id);
        model.addAttribute("usuario", currentUser.actual(principal));
        model.addAttribute("clase", clase);
        model.addAttribute("editable", profesorService.esEditable(clase));
        model.addAttribute("alumnos", profesorService.alumnosDeClase(id));
        model.addAttribute("notas", profesorService.notasDeClase(id));
        model.addAttribute("tipos", TipoNota.values());
        return "profesor/clase";
    }

    @PostMapping("/clase/{id}/nota")
    public String registrarNota(@PathVariable int id,
                                @RequestParam int codigoAlumno,
                                @RequestParam TipoNota tipo,
                                @RequestParam int calificacion,
                                RedirectAttributes ra) {
        try {
            profesorService.registrarNota(id, codigoAlumno, tipo, calificacion);
            ra.addFlashAttribute("msg", "Nota " + tipo + " registrada para el alumno " + codigoAlumno + ".");
        } catch (IllegalStateException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/profesor/clase/" + id;
    }
}
