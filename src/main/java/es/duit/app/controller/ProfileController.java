package es.duit.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import es.duit.app.dto.EditProfileDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.service.AppUserService;

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
    public String updateProfile(
            @Validated(EditProfileDTO.UserProfileGroup.class) @ModelAttribute("editProfileDTO") EditProfileDTO editProfileDTO,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {

        AppUser user = appUserService.getCurrentUser();

        if (errors.hasErrors()) {
            model.addAttribute("editProfileDTO", editProfileDTO);
            model.addAttribute("user", user);
            return "profile/profileUser";
        }

        try {
            appUserService.updateUserProfile(editProfileDTO);
            flash.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("errors", e.getMessage());
            model.addAttribute("editProfileDTO", editProfileDTO);
            model.addAttribute("user", user);
            return "profile/profileUser";
        } catch (Exception e) {
            flash.addFlashAttribute("errors", "Error inesperado al actualizar el perfil");
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

    // ============================================================================
    // PROCESA LA ACTUALIZACIÓN DEL PERFIL PROFESIONAL
    // ============================================================================
    @PostMapping({ "/professional", "/profesional" })
    public String updateProfessionalProfile(
            @Validated(EditProfileDTO.ProfessionalProfileGroup.class) @ModelAttribute("editProfileDTO") EditProfileDTO editProfileDTO,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {

        AppUser user = appUserService.getCurrentUser();

        if (errors.hasErrors()) {
            model.addAttribute("editProfileDTO", editProfileDTO);
            model.addAttribute("user", user);
            return "profile/profileProfessional";
        }

        try {
            appUserService.updateProfessionalProfile(editProfileDTO);
            flash.addFlashAttribute("success", "Perfil profesional actualizado correctamente");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("errors", e.getMessage());
            model.addAttribute("editProfileDTO", editProfileDTO);
            model.addAttribute("user", user);
            return "profile/profileProfessional";
        } catch (Exception e) {
            flash.addFlashAttribute("errors", "Error inesperado al actualizar el perfil profesional");
        }

        return "redirect:/profile/professional";
    }

    // ============================================================================
    // ELIMINA LA CUENTA DEL USUARIO AUTENTICADO (DESACTIVACIÓN)
    // ============================================================================
    @PostMapping("/delete-account")
    public String deleteAccount(
            @RequestParam(value = "from", required = false, defaultValue = "user") String from,
            Authentication authentication,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes flash) {

        String backTo = "professional".equalsIgnoreCase(from)
                ? "redirect:/profile/professional"
                : "redirect:/profile/edit";

        try {
            appUserService.deactivateCurrentUser();
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            flash.addFlashAttribute("success", "Tu cuenta se eliminó correctamente.");
            return "redirect:/login";

        } catch (IllegalArgumentException error) {
            flash.addFlashAttribute("errors", error.getMessage());
            return backTo;

        } catch (Exception error) {
            flash.addFlashAttribute("errors", "No se pudo eliminar la cuenta. Inténtalo de nuevo.");
            return backTo;
        }
    }
}