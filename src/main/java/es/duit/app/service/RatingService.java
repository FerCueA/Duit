package es.duit.app.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import es.duit.app.repository.RatingRepository;
import es.duit.app.repository.ServiceJobRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO DE VALORACIONES - GESTIONA VALORACIONES ENTRE USUARIOS
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ServiceJobRepository serviceJobRepository;

    // ============================================================================
    // CREA UNA VALORACIÓN DE UN TRABAJO COMPLETADO CON VALIDACIONES
    // ============================================================================
    public Rating createRating(Long jobId, Integer score, String comment, AppUser usuario) {
        validateScore(score);

        ServiceJob trabajo = getJobById(jobId);

        validateUserPermission(trabajo, usuario);

        validateJobIsCompleted(trabajo);

        Rating.Type type = determineRatingType(trabajo, usuario);

        validateNoExistingRating(trabajo, type);

        Rating nuevaValoracion = buildRating(trabajo, type, score, comment);

        if (nuevaValoracion == null) {
            throw new IllegalStateException("Error al crear la valoración");
        }

        return ratingRepository.save(nuevaValoracion);
    }

    // ============================================================================
    // VALIDA QUE LA PUNTUACIÓN ESTÉ ENTRE 1 Y 5
    // ============================================================================
    private void validateScore(Integer score) {
        // Verificar que la puntuación no es nula
        if (score == null) {
            throw new IllegalArgumentException("La puntuación es requerida");
        }

        // Verificar que está entre 1 y 5
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5");
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO TIENE PERMISO PARA VALORAR
    // ============================================================================
    private void validateUserPermission(ServiceJob trabajo, AppUser usuario) {
        // Obtener el ID del usuario actual
        Long usuarioId = usuario.getId();

        // Obtener el ID del cliente del trabajo
        Long clienteId = trabajo.getClient().getId();

        // Obtener el ID del profesional del trabajo
        Long profesionalId = trabajo.getProfessional().getId();

        // Verificar si el usuario es cliente o profesional
        boolean esCliente = usuarioId.equals(clienteId);
        boolean esProfesional = usuarioId.equals(profesionalId);

        // Si no es ninguno de los dos, no tiene permiso
        if (!esCliente && !esProfesional) {
            throw new IllegalArgumentException("No tienes permiso para valorar este trabajo");
        }
    }

    // ============================================================================
    // VALIDA QUE EL TRABAJO ESTÁ EN ESTADO COMPLETADO
    // ============================================================================
    private void validateJobIsCompleted(ServiceJob trabajo) {
        // Obtener el estado del trabajo
        ServiceJob.Status estado = trabajo.getStatus();

        // Verificar que está completado
        boolean estaCompletado = estado == ServiceJob.Status.COMPLETED;

        if (!estaCompletado) {
            throw new IllegalArgumentException("Solo se pueden valorar trabajos completados");
        }
    }

    // ============================================================================
    // DETERMINA EL TIPO DE VALORACIÓN (CLIENTE VALORA A PROFESIONAL O VICEVERSA)
    // ============================================================================
    private Rating.Type determineRatingType(ServiceJob trabajo, AppUser usuario) {
        // Obtener el ID del usuario
        Long usuarioId = usuario.getId();

        // Obtener el ID del cliente
        Long clienteId = trabajo.getClient().getId();

        // Si el usuario es el cliente, valora al profesional
        boolean esCliente = usuarioId.equals(clienteId);

        if (esCliente) {
            return Rating.Type.CLIENT_TO_PROFESSIONAL;
        } else {
            // Si no es cliente, entonces es profesional, así que valora al cliente
            return Rating.Type.PROFESSIONAL_TO_CLIENT;
        }
    }

    // ============================================================================
    // VALIDA QUE EL USUARIO NO HA VALORADO ESTE TRABAJO PREVIAMENTE
    // ============================================================================
    private void validateNoExistingRating(ServiceJob trabajo, Rating.Type type) {
        // Buscar si existe una valoración del mismo tipo para este trabajo
        boolean yaExisteValoracion = ratingRepository.findByJobAndType(trabajo, type).isPresent();

        if (yaExisteValoracion) {
            throw new IllegalArgumentException("Ya has valorado este trabajo");
        }
    }

    // ============================================================================
    // OBTIENE UN TRABAJO POR ID
    // ============================================================================
    private ServiceJob getJobById(Long jobId) {
        // Validar que el ID no sea nulo
        if (jobId == null) {
            throw new IllegalArgumentException("El ID del trabajo es requerido");
        }

        // Buscar el trabajo en la base de datos
        ServiceJob trabajo = serviceJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Trabajo no encontrado"));

        return trabajo;
    }

    // ============================================================================
    // CONSTRUYE UN OBJETO RATING CON LOS DATOS PROPORCIONADOS
    // ============================================================================
    private Rating buildRating(ServiceJob trabajo, Rating.Type type, Integer score, String comment) {
        // Crear nueva instancia de Rating
        Rating nuevaValoracion = new Rating();

        // Asignar el trabajo
        nuevaValoracion.setJob(trabajo);

        // Asignar el tipo de valoración
        nuevaValoracion.setType(type);

        // Asignar la puntuación
        nuevaValoracion.setScore(score);

        // Procesar el comentario (si está vacío, asignamos null)
        String comentarioFinal = null;
        if (comment != null && !comment.trim().isEmpty()) {
            comentarioFinal = comment;
        }
        nuevaValoracion.setComment(comentarioFinal);

        // Asignar el estado como PUBLISHED
        nuevaValoracion.setStatus(Rating.Status.PUBLISHED);

        return nuevaValoracion;
    }
}
