package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.RatingRepository;
import es.duit.app.repository.ServiceJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ServiceJobRepository serviceJobRepository;

    public RatingService(RatingRepository ratingRepository, 
                         ServiceJobRepository serviceJobRepository) {
        this.ratingRepository = ratingRepository;
        this.serviceJobRepository = serviceJobRepository;
    }

    // Crear una valoración
    public Rating crearValoracion(Long jobId, Integer score, String comment, AppUser usuario) {
        
        // Validar score
        if (score == null || score < 1 || score > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }

        // Obtener el trabajo
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
            .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        // Validar que el usuario pertenece al trabajo
        if (!trabajo.getClient().getId().equals(usuario.getId()) && 
            !trabajo.getProfessional().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("No tienes permiso para valorar este trabajo");
        }

        // Validar que el trabajo está completado
        if (trabajo.getStatus() != ServiceJob.Status.COMPLETED) {
            throw new IllegalArgumentException("Solo se pueden valorar trabajos completados");
        }

        // Determinar el tipo de valoración
        Rating.Type type = trabajo.getClient().getId().equals(usuario.getId())
            ? Rating.Type.CLIENT_TO_PROFESSIONAL
            : Rating.Type.PROFESSIONAL_TO_CLIENT;

        // Validar que no haya valorado antes
        if (ratingRepository.findByJobAndType(trabajo, type).isPresent()) {
            throw new IllegalArgumentException("Ya has valorado este trabajo");
        }

        // Crear la valoración
        Rating nuevaValoracion = new Rating();
        nuevaValoracion.setJob(trabajo);
        nuevaValoracion.setType(type);
        nuevaValoracion.setScore(score);
        nuevaValoracion.setComment(comment != null && !comment.trim().isEmpty() ? comment : null);
        nuevaValoracion.setStatus(Rating.Status.PUBLISHED);

        return ratingRepository.save(nuevaValoracion);
    }
}
