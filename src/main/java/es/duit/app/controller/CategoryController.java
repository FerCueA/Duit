package es.duit.app.controller;

import es.duit.app.dto.CategoryDTO;
import es.duit.app.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// ============================================================================
// CONTROLADOR DE CATEGORÍAS - GESTIONA ADMINISTRACIÓN DE CATEGORÍAS DE SERVICIOS
// ============================================================================
@Controller
@RequestMapping("/admin")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // ============================================================================
    // INICIALIZA EL DTO DE CATEGORÍA PARA EL FORMULARIO
    // ============================================================================
    @ModelAttribute("categoryDTO")
    public CategoryDTO inicializarCategoryDTO() {
        return new CategoryDTO();
    }

    // ============================================================================
    // MUESTRA LA PÁGINA DE GESTIÓN DE CATEGORÍAS
    // ============================================================================
    @GetMapping("/categories")
    public String mostrarPaginaCategorias(Model model) {
        // Obtener todas las categorías ordenadas y enviarlas a la vista
        model.addAttribute("categories", categoryService.findAllOrdered());
        return "admin/categories";
    }

    // ============================================================================
    // PROCESA EL GUARDADO DE CATEGORÍAS (CREAR O EDITAR)
    // ============================================================================
    @PostMapping("/categories")
    public String procesarGuardadoCategoria(@Valid @ModelAttribute CategoryDTO categoryDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // Validar errores de formulario
            if (bindingResult.hasErrors()) {
                // Volver al formulario con las categorías y los errores
                model.addAttribute("categories", categoryService.findAllOrdered());
                return "admin/categories";
            }

            // Guardar la categoría usando el servicio
            categoryService.save(categoryDTO);

            // Preparar mensaje de éxito según si es creación o edición
            boolean esNuevaCategoria = categoryDTO.getId() == null;
            String mensajeExito = esNuevaCategoria ? "Categoría creada correctamente" : "Categoría actualizada correctamente";
            redirectAttributes.addFlashAttribute("success", mensajeExito);

        } catch (IllegalArgumentException error) {
            // Manejar errores de validación de negocio
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            redirectAttributes.addFlashAttribute("categoryDTO", categoryDTO);
        }

        return "redirect:/admin/categories";
    }

    // ============================================================================
    // PREPARA UNA CATEGORÍA PARA EDICIÓN
    // ============================================================================
    @GetMapping("/edit/{id}")
    public String prepararEdicionCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            // Buscar la categoría por ID
            CategoryDTO categoriaEncontrada = categoryService.findById(id);

            // Verificar si la categoría existe
            if (categoriaEncontrada == null) {
                String mensajeError = "Categoría no encontrada";
                redirectAttributes.addFlashAttribute("error", mensajeError);
                return "redirect:/admin/categories";
            }

            // Preparar datos para edición
            redirectAttributes.addFlashAttribute("categoryDTO", categoriaEncontrada);
            redirectAttributes.addFlashAttribute("editMode", true);

        } catch (IllegalArgumentException error) {
            // Manejar errores en la búsqueda
            redirectAttributes.addFlashAttribute("error", error.getMessage());
        }

        return "redirect:/admin/categories";
    }

    // ============================================================================
    // ELIMINA UNA CATEGORÍA ESPECÍFICA
    // ============================================================================
    @GetMapping("/delete/{id}")
    public String eliminarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            // Intentar eliminar la categoría
            categoryService.deleteById(id);

            // Preparar mensaje de éxito
            String mensajeExito = "Categoría eliminada correctamente";
            redirectAttributes.addFlashAttribute("success", mensajeExito);

        } catch (Exception error) {
            // Manejar errores en la eliminación
            String mensajeError = "No se puede eliminar la categoría";
            redirectAttributes.addFlashAttribute("error", mensajeError);
        }

        return "redirect:/admin/categories";
    }

    // ============================================================================
    // CAMBIA EL ESTADO ACTIVO/INACTIVO DE UNA CATEGORÍA
    // ============================================================================
    @GetMapping("/toggle/{id}")
    public String cambiarEstadoCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {

        try {
            // Cambiar el estado de la categoría
            categoryService.toggleStatus(id);

            // Preparar mensaje de éxito
            String mensajeExito = "Estado cambiado correctamente";
            redirectAttributes.addFlashAttribute("success", mensajeExito);

        } catch (IllegalArgumentException error) {
            // Manejar errores en el cambio de estado
            redirectAttributes.addFlashAttribute("error", error.getMessage());
        }

        return "redirect:/admin/categories";
    }
}
