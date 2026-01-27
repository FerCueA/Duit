package es.duit.app.repository;

import es.duit.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Buscar todas las categorías activas con JOIN FETCH 
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.name ASC")
    List<Category> findAllActiveCategories();

    // JPA genera automáticamente: findByActiveOrderByName(Boolean active)
    // JPA genera automáticamente: findByNameIgnoreCase(String name)
}