package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.duit.app.dto.EditProfileDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.service.AppUserService;
import jakarta.validation.Valid;

// ============================================================================
// CONTROLADOR DE PERFIL - GESTIONA EDICIÓN Y VISUALIZACIÓN DE PERFILES
// ============================================================================
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AppUserService appUserService;

    public ProfileController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    // ============================================================================
    // INICIALIZA EL DTO DE EDICIÓN DE PERFIL
    // ============================================================================
    @ModelAttribute("editProfileDTO")
    public EditProfileDTO editProfileDTO() {
        return new EditProfileDTO();
    }

    // ============================================================================
    // MUESTRA EL FORMULARIO DE EDICIÓN DE PERFIL
    // ============================================================================
    @GetMapping("/edit")
    public String edit(Model model) {

        AppUser user = appUserService.getCurrentUser();
        EditProfileDTO dto = new EditProfileDTO(user);

        model.addAttribute("editProfileDTO", dto);
        model.addAttribute("user", user);

        return "profile/profileUser";
    }

    // ============================================================================
    // PROCESA LA ACTUALIZACIÓN DEL PERFIL DE USUARIO
    // ============================================================================
    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute("editProfileDTO") EditProfileDTO editProfileDTO,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {

        AppUser user = appUserService.getCurrentUser();

        try {
            if (errors.hasErrors()) {
                model.addAttribute("editProfileDTO", editProfileDTO);
                model.addAttribute("user", user);
                return "profile/profileUser";
            }
            appUserService.updateUserProfile(editProfileDTO);
            flash.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("errors", e.getMessage());
            flash.addFlashAttribute("editProfileDTO", editProfileDTO);
            return "redirect:/profile/edit";
        }

        return "redirect:/profile/edit";
    }

    // ============================================================================
    // MUESTRA EL PERFIL PROFESIONAL
    // ============================================================================
    @GetMapping({ "/professional", "/profesional" })
    public String professional(Model model) {

        AppUser userPro = appUserService.getCurrentUser();
        EditProfileDTO dto = new EditProfileDTO(userPro);

        model.addAttribute("editProfileDTO", dto);
        model.addAttribute("user", userPro);
        return "profile/profileProfessional";
    }
}