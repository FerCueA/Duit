
package es.duit.app.controller;

import es.duit.app.dto.CategoryDTO;
import es.duit.app.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/admin/categorias")
    public String mostrarListaDeCategorias(Model modelo) {
        List<CategoryDTO> categorias = categoryService.findAllOrdered();
        modelo.addAttribute("categorias", categorias);

        if (!modelo.containsAttribute("categoryDTO")) {
            modelo.addAttribute("categoryDTO", new CategoryDTO());
        }

        return "admin/categorias";
    }

    @PostMapping("/admin/categorias")
    public String guardarCategoria(
            @Valid @ModelAttribute CategoryDTO datosCategoria,
            BindingResult resultadoValidacion,
            RedirectAttributes mensajesFlash) {

        if (resultadoValidacion.hasErrors()) {
            mensajesFlash.addFlashAttribute("org.springframework.validation.BindingResult.categoryDTO",
                    resultadoValidacion);
            mensajesFlash.addFlashAttribute("categoryDTO", datosCategoria);
            return "redirect:/admin/categorias";
        }

        try {
            categoryService.save(datosCategoria);
            String mensaje = datosCategoria.getId() == null ? "Categoría creada correctamente"
                    : "Categoría actualizada correctamente";
            mensajesFlash.addFlashAttribute("success", mensaje);
        } catch (IllegalArgumentException error) {
            mensajesFlash.addFlashAttribute("error", error.getMessage());
            mensajesFlash.addFlashAttribute("categoryDTO", datosCategoria);
        }

        return "redirect:/admin/categorias";
    }

    @GetMapping("/admin/categorias/edit/{id}")
    public String prepararEdicionCategoria(@PathVariable("id") Long idCategoria, RedirectAttributes mensajesFlash) {
        try {
            CategoryDTO categoria = categoryService.findById(idCategoria)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            mensajesFlash.addFlashAttribute("categoryDTO", categoria);
            mensajesFlash.addFlashAttribute("editMode", true);
        } catch (IllegalArgumentException error) {
            mensajesFlash.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/admin/categorias";
    }

    @GetMapping("/admin/categorias/delete/{id}")
    public String eliminarCategoria(@PathVariable("id") Long idCategoria, RedirectAttributes mensajesFlash) {
        try {
            categoryService.deleteById(idCategoria);
            mensajesFlash.addFlashAttribute("success", "Categoría eliminada correctamente");
        } catch (Exception error) {
            mensajesFlash.addFlashAttribute("error", "No se puede eliminar la categoría");
        }
        return "redirect:/admin/categorias";
    }

    @GetMapping("/admin/categorias/toggle/{id}")
    public String cambiarEstadoCategoria(@PathVariable("id") Long idCategoria, RedirectAttributes mensajesFlash) {
        try {
            categoryService.toggleActive(idCategoria);
            mensajesFlash.addFlashAttribute("success", "Estado cambiado correctamente");
        } catch (IllegalArgumentException error) {
            mensajesFlash.addFlashAttribute("error", error.getMessage());
        }
        return "redirect:/admin/categorias";
    }
}
