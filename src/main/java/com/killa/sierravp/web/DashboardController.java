package com.killa.sierravp.web;

import com.killa.sierravp.domain.Usuario;
import com.killa.sierravp.service.CurrentUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class DashboardController {

    private final CurrentUserService currentUser;

    public DashboardController(CurrentUserService currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        Usuario u = currentUser.actual(principal);
        model.addAttribute("usuario", u);
        model.addAttribute("rol", u != null ? u.getRol() : "");
        return "dashboard";
    }
}
