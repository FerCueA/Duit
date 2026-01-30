package es.duit.app.controller;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Rating;
import es.duit.app.entity.ServiceJob;
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

@Controller
@RequestMapping("/shared")
public class SharedController {

    private final ServiceJobRepository serviceJobRepository;
    private final RatingRepository ratingRepository;
    private final AuthService authService;

    public SharedController(ServiceJobRepository serviceJobRepository, RatingRepository ratingRepository, AuthService authService) {
        this.serviceJobRepository = serviceJobRepository;
        this.ratingRepository = ratingRepository;
        this.authService = authService;
    }

    @GetMapping("/historial")
    public String historial() {
        return "shared/historial";
    }

    @GetMapping("/valoraciones")
    public String valoraciones(Authentication auth, Model model, @RequestParam(required = false) Long jobId) {
        AppUser usuarioLogueado = authService.obtenerUsuarioAutenticado(auth);
        
        // Trabajos completados del usuario
        List<ServiceJob> trabajosCompletados = serviceJobRepository.findByCliente(usuarioLogueado);
        trabajosCompletados.addAll(serviceJobRepository.findByProfesional(usuarioLogueado));
        trabajosCompletados.removeIf(trabajo -> trabajo.getStatus() != ServiceJob.Status.COMPLETED);
        
        // Separar en pendientes y finalizadas
        List<ServiceJob> trabajosPendientes = new ArrayList<>();
        List<ServiceJob> trabajosFinalizadas = new ArrayList<>();
        
        for (ServiceJob trabajo : trabajosCompletados) {
            // Verificar si ambas partes han valorado
            boolean clienteHaValorado = ratingRepository.findByJobAndType(trabajo, Rating.Type.CLIENT_TO_PROFESSIONAL).isPresent();
            boolean profesionalHaValorado = ratingRepository.findByJobAndType(trabajo, Rating.Type.PROFESSIONAL_TO_CLIENT).isPresent();
            
            if (clienteHaValorado && profesionalHaValorado) {
                trabajosFinalizadas.add(trabajo);
            } else {
                trabajosPendientes.add(trabajo);
            }
        }
        
        model.addAttribute("trabajosPendientes", trabajosPendientes);
        model.addAttribute("trabajosFinalizadas", trabajosFinalizadas);
        
        // Cargar trabajo específico
        if (jobId != null) {
            ServiceJob trabajoEspecifico = serviceJobRepository.findById(jobId).orElse(null);
            if (trabajoEspecifico != null) {
                model.addAttribute("trabajoSeleccionado", trabajoEspecifico);
            }
        }
        
        return "shared/valoraciones";
    }

    @GetMapping("/ayuda")
    public String ayuda() {
        return "shared/ayuda";
    }
}