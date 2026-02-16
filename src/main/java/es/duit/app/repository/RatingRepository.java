package es.duit.app.repository;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByJob(ServiceJob job);

    Optional<Rating> findByJobAndType(ServiceJob job, Rating.Type type);

    @Query("select r from Rating r where r.type = :type and r.status = :status and r.job.application.professional.user = :user order by r.ratedAt desc")
    List<Rating> findRatingsForProfessional(@Param("user") AppUser user,
            @Param("type") Rating.Type type,
            @Param("status") Rating.Status status);
}
