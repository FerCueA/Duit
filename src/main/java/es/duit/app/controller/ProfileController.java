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

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final AppUserService appUserService;

    public ProfileController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }

    @ModelAttribute("EditProfileDTO")
    public EditProfileDTO editProfileDTO() {
        return new EditProfileDTO();
    }

    @GetMapping("/edit")
    public String edit(Model model) {

        AppUser user = appUserService.getActualUser();
        EditProfileDTO dto = new EditProfileDTO(user);

        model.addAttribute("editProfileDTO", dto);
        model.addAttribute("user", user);

        return "profile/profileUser";
    }

    @GetMapping("/profesional")
    public String professional() {
        return "profile/profileProfessional";
    }

    @PostMapping("/edit")
    public String updateProfile(@Valid @ModelAttribute EditProfileDTO editProfileDTO,
            BindingResult errors,
            Model model,
            RedirectAttributes flash) {

        AppUser user = appUserService.getActualUser();

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
}