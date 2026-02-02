package es.duit.app.service;

import es.duit.app.dto.CategoryDTO;
import es.duit.app.entity.Category;
import es.duit.app.repository.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// ============================================================================
// SERVICIO DE CATEGORÍAS - GESTIONA CATEGORÍAS DE SERVICIOS
// ============================================================================
@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // ============================================================================
    // OBTIENE TODAS LAS CATEGORÍAS ORDENADAS POR ID
    // ============================================================================
    public List<CategoryDTO> findAllOrdered() {
        // Definir ordenación ascendente
        Sort sort = Sort.by(Sort.Direction.ASC, "id");

        // Obtener categorías de la BD
        List<Category> categories = categoryRepository.findAll(sort);
        List<CategoryDTO> categoryDTOs = new ArrayList<>();

        // Convertir cada categoría a DTO
        for (Category category : categories) {
            categoryDTOs.add(new CategoryDTO(category));
        }

        return categoryDTOs;
    }

    // ============================================================================
    // BUSCA UNA CATEGORÍA POR ID
    // ============================================================================
    public CategoryDTO findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID de categoría requerido");
        }
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        // Si no existe, devolver null
        if (categoryOptional.isEmpty()) {
            return null;
        }

        // Convertir categoría a DTO y devolver
        Category category = categoryOptional.get();
        return new CategoryDTO(category);
    }

    // ============================================================================
    // GUARDA O ACTUALIZA UNA CATEGORÍA
    // ============================================================================
    public CategoryDTO save(CategoryDTO categoryDTO) {
        // Verificar si ya existe otra categoría con el mismo nombre
        String nombre = categoryDTO.getName();
        Long id = categoryDTO.getId();

        boolean nombreDuplicado = isDuplicateName(nombre, id);
        if (nombreDuplicado) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }

        // Crear nueva categoría o actualizar existente
        Category category = new Category();
        category.setId(categoryDTO.getId());
        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        category.setActive(categoryDTO.getActive());

        // Guardar en base de datos
        Category saved = categoryRepository.save(category);

        // Convertir entidad guardada a DTO y devolver
        return new CategoryDTO(saved);
    }

    // ============================================================================
    // ELIMINA UNA CATEGORÍA POR ID
    // ============================================================================
    public void deleteById(Long id) {
        // Verificar si existe la categoría
        if (id == null) {
            throw new IllegalArgumentException("ID de categoría requerido");
        }
        boolean existe = categoryRepository.existsById(id);
        if (!existe) {
            throw new IllegalArgumentException("La categoría no existe");
        }

        // Eliminar de la base de datos
        categoryRepository.deleteById(id);
    }

    // ============================================================================
    // CAMBIA EL ESTADO ACTIVO/INACTIVO DE UNA CATEGORÍA
    // ============================================================================
    public void toggleStatus(Long id) {
        // Validar el ID
        if (id == null) {
            throw new IllegalArgumentException("ID de categoría requerido");
        }
        
        // Buscar la categoría por ID
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        // Validar que la categoría existe
        if (categoryOptional.isEmpty()) {
            throw new IllegalArgumentException("La categoría no existe");
        }

        // Obtener la categoría
        Category category = categoryOptional.get();

        // Cambiar el estado de activa/inactiva
        Boolean estadoActual = category.getActive();
        Boolean nuevoEstado = !Boolean.TRUE.equals(estadoActual);
        category.setActive(nuevoEstado);

        // Guardar el cambio en la BD
        categoryRepository.save(category);
    }

    // ============================================================================
    // VALIDA SI EXISTE UNA CATEGORÍA CON EL MISMO NOMBRE Y DIFERENTE ID
    // ============================================================================
    private boolean isDuplicateName(String name, Long id) {
        // Obtener todas las categorías de la BD
        List<Category> todasLasCategorias = categoryRepository.findAll();

        // Iterar y buscar si existe una con el mismo nombre pero diferente ID
        for (Category cat : todasLasCategorias) {
            String nombreCategoria = cat.getName();
            Long idCategoria = cat.getId();

            // Comparar nombres (ignorando mayúsculas/minúsculas)
            boolean mismoNombre = nombreCategoria.equalsIgnoreCase(name);
            boolean diferenteId = !idCategoria.equals(id);

            // Si tiene el mismo nombre y diferente ID, existe duplicado
            if (mismoNombre && diferenteId) {
                return true;
            }
        }

        // No hay duplicados, retornar false
        return false;
    }
}