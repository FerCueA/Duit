package es.duit.app.service;

import es.duit.app.dto.CategoryDTO;
import es.duit.app.entity.Category;
import es.duit.app.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public List<CategoryDTO> findAllOrdered() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return categoryRepository.findAll(sort)
                .stream()
                .map(CategoryDTO::new)
                .collect(Collectors.toList());
    }

    public Optional<CategoryDTO> findById(Long id) {
        return categoryRepository.findById(id).map(CategoryDTO::new);
    }

    public CategoryDTO save(CategoryDTO categoryDTO) {
        if (existsByNameAndNotId(categoryDTO.getName(), categoryDTO.getId())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        Category category = categoryDTO.toEntity();
        Category saved = categoryRepository.save(category);
        return new CategoryDTO(saved);
    }

    public void deleteById(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("La categoría no existe");
        }
        categoryRepository.deleteById(id);
    }

    public void toggleActive(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La categoría no existe"));
        category.setActive(!Boolean.TRUE.equals(category.getActive()));
        categoryRepository.save(category);
    }

    private boolean existsByNameAndNotId(String name, Long id) {
        return categoryRepository.findAll().stream()
                .anyMatch(cat -> cat.getName().equalsIgnoreCase(name) && !cat.getId().equals(id));
    }
}