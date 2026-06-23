package com.killa.sierravp.web;

import com.killa.sierravp.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.LinkedHashMap;

/** Capa analítica: ETL al DataWareHouse, reportes OLAP y capas FTP/Mirror. */
@Controller
@RequestMapping("/analitica")
public class AnaliticaController {

    private final EtlService etlService;
    private final OlapService olapService;
    private final FtpMirrorService ftpMirrorService;
    private final CurrentUserService currentUser;

    public AnaliticaController(EtlService etlService, OlapService olapService,
                              FtpMirrorService ftpMirrorService, CurrentUserService currentUser) {
        this.etlService = etlService;
        this.olapService = olapService;
        this.ftpMirrorService = ftpMirrorService;
        this.currentUser = currentUser;
    }

    @GetMapping
    public String index(Principal principal, Model model) {
        model.addAttribute("usuario", currentUser.actual(principal));
        model.addAttribute("totalHechoNota", olapService.totalHechoNota());
        model.addAttribute("totalHechoCra", olapService.totalHechoCra());
        model.addAttribute("ftpDir", ftpMirrorService.ftpPath());
        model.addAttribute("mirrorDir", ftpMirrorService.mirrorPath());
        return "analitica/index";
    }

    @PostMapping("/etl")
    public String etl(RedirectAttributes ra) {
        LinkedHashMap<String, Object> r = etlService.ejecutar();
        ra.addFlashAttribute("msg", "ETL ejecutado. " + r);
        return "redirect:/analitica";
    }

    @PostMapping("/ftp")
    public String ftp(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", ftpMirrorService.publicarEnFtp());
        return "redirect:/analitica";
    }

    @PostMapping("/mirror")
    public String mirror(RedirectAttributes ra) {
        ra.addFlashAttribute("msg", ftpMirrorService.replicarEnMirror());
        return "redirect:/analitica";
    }

    @GetMapping("/olap")
    public String olap(@RequestParam(defaultValue = "10") int topN,
                       @RequestParam(required = false) Integer codigoAlumno,
                       @RequestParam(required = false) Integer idCurso,
                       Principal principal, Model model) {
        model.addAttribute("usuario", currentUser.actual(principal));

        // Reporte 1, 2, 5 (siempre)
        model.addAttribute("promedioFacPer", olapService.promedioFacultadPeriodo());
        model.addAttribute("topN", topN);
        model.addAttribute("rankingTopN", olapService.topNPorEscuela(topN));
        model.addAttribute("distribucion", olapService.distribucionPorTipo());

        // Reporte 3 (histórico por alumno) bajo demanda
        model.addAttribute("codigoAlumno", codigoAlumno);
        if (codigoAlumno != null) {
            model.addAttribute("historico", olapService.historicoCra(codigoAlumno));
        }

        // Reporte 4 (estadísticas de curso) bajo demanda
        model.addAttribute("cursos", olapService.cursosDisponibles());
        model.addAttribute("idCurso", idCurso);
        if (idCurso != null) {
            model.addAttribute("statsCurso", olapService.estadisticasCurso(idCurso));
        }
        return "analitica/olap";
    }
}
