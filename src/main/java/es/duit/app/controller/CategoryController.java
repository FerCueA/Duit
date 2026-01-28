
package es.duit.app.controller;

import es.duit.app.entity.Category;
import es.duit.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categorias")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", new Category());
        return "admin/categorias";
    }

    @PostMapping
    public String save(@ModelAttribute Category category) {
        categoryRepository.save(category);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Category category = categoryRepository.findById(id).orElse(null);
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("category", category);
        return "admin/categorias";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categorias";
    }

    @GetMapping("/toggle/{id}")
    public String toggleActive(@PathVariable Long id) {
        categoryRepository.findById(id).ifPresent(cat -> {
            cat.setActive(!Boolean.TRUE.equals(cat.getActive()));
            categoryRepository.save(cat);
        });
        return "redirect:/admin/categorias";
    }
}
