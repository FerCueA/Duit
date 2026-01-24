package es.duit.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.duit.app.entity.AppUser;
import es.duit.app.repository.AppUserRepository;

@Controller
public class DashboardController {

    @Autowired
    private AppUserRepository appUserRepository;

    @GetMapping("/home")
    public String home(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        AppUser usuario = appUserRepository.findByUsername(username).orElse(null);
        model.addAttribute("usuario", usuario);
        return "dashboard/home";
    }

}
