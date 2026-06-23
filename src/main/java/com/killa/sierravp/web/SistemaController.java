package com.killa.sierravp.web;

import com.killa.sierravp.service.CraService;
import com.killa.sierravp.service.CurrentUserService;
import com.killa.sierravp.service.RankingService;
import com.killa.sierravp.service.SeedService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Procesos batch del OLTP (CU-05 CRA, CU-06 ranking) + poblado de datos. */
@Controller
@RequestMapping("/sistema")
public class SistemaController {

    private final SeedService seedService;
    private final CraService craService;
    private final RankingService rankingService;
    private final CurrentUserService currentUser;

    public SistemaController(SeedService seedService, CraService craService,
                             RankingService rankingService, CurrentUserService currentUser) {
        this.seedService = seedService;
        this.craService = craService;
        this.rankingService = rankingService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String index(Principal principal, Model model) {
        model.addAttribute("usuario", currentUser.actual(principal));
        model.addAttribute("yaPoblado", seedService.yaPoblado());
        return "sistema/index";
    }

    @PostMapping("/poblar")
    public String poblar(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", seedService.poblar());
        return "redirect:/sistema";
    }

    @PostMapping("/cra")
    public String calcularCra(@RequestParam(defaultValue = "2026-I") String periodo,
                              RedirectAttributes ra) {
        int n = craService.calcularTodas(periodo);
        ra.addFlashAttribute("msg", "CRA normalizado calculado para " + n
                + " registros alumno-clase del periodo " + periodo + ".");
        return "redirect:/sistema";
    }

    @PostMapping("/ranking")
    public String generarRanking(RedirectAttributes ra) {
        int n = rankingService.generarRanking();
        ra.addFlashAttribute("msg", "Ranking generado: " + n + " alumnos posicionados por escuela.");
        return "redirect:/sistema";
    }
}
