package es.duit.app.repository;

import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByJob(ServiceJob job);
    
    Optional<Rating> findByJobAndType(ServiceJob job, Rating.Type type);
}

