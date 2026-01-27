package es.duit.app.repository;

import es.duit.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Buscar categorías activas
    List<Category> findByActiveTrue();

}