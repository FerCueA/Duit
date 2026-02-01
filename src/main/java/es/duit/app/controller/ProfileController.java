package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import es.duit.app.dto.EditarPerfilDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.service.AppUserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AppUserService appUserService;

    public ProfileController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @GetMapping("/editar")
    public String editar(Model model) {

        AppUser user = appUserService.obtenerUsuarioActual();
        EditarPerfilDTO dto = new EditarPerfilDTO(user);

        model.addAttribute("editarPerfilDTO", dto);
        model.addAttribute("user", user);

        return "profile/editarPerfil";
    }

    @GetMapping("/profesional")
    public String profesional() {
        return "profile/editarProfesional";
    }
}