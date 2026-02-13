package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
import es.duit.app.service.HistoryService;
import es.duit.app.repository.RatingRepository;
import es.duit.app.repository.ServiceJobRepository;
import es.duit.app.service.AuthService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

// ============================================================================
// CONTROLADOR COMPARTIDO - GESTIONA PÁGINAS ACCESIBLES POR TODOS LOS USUARIOS
// ============================================================================
@Controller
@RequestMapping("/shared")
public class SharedController {

    private final ServiceJobRepository serviceJobRepository;
    private final RatingRepository ratingRepository;
    private final AuthService authService;
    private final HistoryService historyService;

    public SharedController(ServiceJobRepository serviceJobRepository,
            RatingRepository ratingRepository,
            AuthService authService,
            HistoryService historyService) {
        this.serviceJobRepository = serviceJobRepository;
        this.ratingRepository = ratingRepository;
        this.authService = authService;
        this.historyService = historyService;
    }

    // ============================================================================
    // PÁGINA DE HISTORIAL DE TRABAJOS
    // ============================================================================
    @GetMapping({ "/history" })
    public String mostrarHistorial(Authentication auth, Model model) {
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);
        model.addAttribute("historial", historyService.getHistoryForUser(usuarioLogueado));
        model.addAttribute("currentUserId", usuarioLogueado.getId());
        return "shared/history";
    }

    // ============================================================================
    // PÁGINA DE VALORACIONES - MUESTRA VALORACIONES PENDIENTES Y FINALIZADAS
    // ============================================================================
    @GetMapping({ "/ratings" })
    public String mostrarValoraciones(Authentication auth, Model model, @RequestParam(required = false) Long jobId) {
        // Obtener el usuario actualmente logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        // Buscar trabajos donde el usuario es cliente
        List<ServiceJob> trabajosComoCliente = serviceJobRepository.findByCliente(usuarioLogueado);

        // Buscar trabajos donde el usuario es profesional
        List<ServiceJob> trabajosComoProfesional = serviceJobRepository.findByProfesional(usuarioLogueado);

        // Combinar ambas listas
        List<ServiceJob> todosLosTrabajos = new ArrayList<>();
        todosLosTrabajos.addAll(trabajosComoCliente);
        todosLosTrabajos.addAll(trabajosComoProfesional);

        // Filtrar solo los trabajos completados
        List<ServiceJob> trabajosCompletados = new ArrayList<>();
        for (ServiceJob trabajo : todosLosTrabajos) {
            boolean estaCompletado = trabajo.getStatus() == ServiceJob.Status.COMPLETED;
            if (estaCompletado) {
                trabajosCompletados.add(trabajo);
            }
        }

        // Separar trabajos en pendientes y finalizadas según valoraciones
        List<ServiceJob> trabajosPendientes = new ArrayList<>();
        List<ServiceJob> trabajosFinalizadas = new ArrayList<>();

        // Revisar cada trabajo completado
        for (ServiceJob trabajo : trabajosCompletados) {
            // Verificar si el cliente ha valorado al profesional
            boolean clienteHaValorado = ratingRepository.findByJobAndType(trabajo, Rating.Type.CLIENT_TO_PROFESSIONAL)
                    .isPresent();

            // Verificar si el profesional ha valorado al cliente
            boolean profesionalHaValorado = ratingRepository
                    .findByJobAndType(trabajo, Rating.Type.PROFESSIONAL_TO_CLIENT).isPresent();

            // Si ambos han valorado, está finalizada
            if (clienteHaValorado && profesionalHaValorado) {
                trabajosFinalizadas.add(trabajo);
            } else {
                // Si falta alguna valoración, está pendiente
                trabajosPendientes.add(trabajo);
            }
        }

        // Agregar listas al modelo para la vista
        model.addAttribute("trabajosPendientes", trabajosPendientes);
        model.addAttribute("trabajosFinalizadas", trabajosFinalizadas);

        // Cargar trabajo específico si se proporciona ID
        cargarTrabajoEspecifico(jobId, model);

        return "shared/ratings";
    }

    // ============================================================================
    // CARGA UN TRABAJO ESPECÍFICO SI SE PROPORCIONA SU ID
    // ============================================================================
    private void cargarTrabajoEspecifico(Long jobId, Model model) {
        // Verificar si se proporcionó un ID de trabajo
        if (jobId == null) {
            return;
        }

        // Buscar el trabajo en la base de datos
        ServiceJob trabajoEspecifico = serviceJobRepository.findById(jobId).orElse(null);

        // Si existe, agregarlo al modelo
        if (trabajoEspecifico != null) {
            model.addAttribute("trabajoSeleccionado", trabajoEspecifico);
        }
    }
}