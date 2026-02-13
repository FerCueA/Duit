package es.duit.app.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;

import es.duit.app.dto.SearchRequestDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.Category;
import es.duit.app.entity.JobApplication;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.repository.CategoryRepository;
import es.duit.app.repository.JobApplicationRepository;
import es.duit.app.repository.ServiceRequestRepository;

// ============================================================================
// SERVICIO DE BÚSQUEDA DE TRABAJOS
// ============================================================================

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SearchService {

    // =========================================================================
    // DEPENDENCIAS Y ATRIBUTOS
    // =========================================================================
    private final ServiceRequestRepository jobRepository;
    private final CategoryRepository categoryRepository;
    private final JobApplicationRepository jobApplicationRepository;

    // ============================================================================
    // PREPARAR DATOS PARA PÁGINA DE BÚSQUEDA
    // ============================================================================
    public void prepareSearchPageData(SearchRequestDTO filters, AppUser user, Model model) {

        // FILTROS DE BÚSQUEDA
        List<ServiceRequest> foundJobs = searchJobs(filters, user);

        // CATEGORÍAS ACTIVAS PARA FILTRO
        List<Category> activeCategories = categoryRepository.findByActiveTrue();

        /// OBTENER CODIGOS POSTALES
        List<ServiceRequest> allJobs = getPublishedJobs();

        // Obtener códigos postales
        Set<String> uniquePostalCodes = new HashSet<>();
        for (ServiceRequest job : allJobs) {
            if (job.getEffectiveServiceAddress() != null) {
                String postalCode = job.getEffectiveServiceAddress().getPostalCode();
                if (postalCode != null && !postalCode.trim().isEmpty()) {
                    uniquePostalCodes.add(postalCode);
                }
            }
        }

        model.addAttribute("ofertas", foundJobs);
        model.addAttribute("categorias", activeCategories);
        model.addAttribute("codigosPostales", uniquePostalCodes);
        model.addAttribute("totalOfertas", foundJobs.size());

        if (filters != null) {
            model.addAttribute("filtrosAplicados", filters);
        }
    }

    // ============================================================================
    // BUSCAR TRABAJOS CON FILTROS
    // IMPORTANTE: SOLO MUESTRA OFERTAS CON ESTADO PUBLISHED
    // EXCLUYE OFERTAS DONDE EL USUARIO YA SE HA POSTULADO (NO RETIRADAS)
    // ============================================================================
    public List<ServiceRequest> searchJobs(SearchRequestDTO filters, AppUser user) {
        List<ServiceRequest> jobs = getPublishedJobs();

        // Obtener IDs de ofertas donde el usuario ya se ha postulado (no retiradas)
        final Set<Long> appliedJobIds;
        if (user != null && user.getProfessionalProfile() != null) {
            List<JobApplication> myApplications = jobApplicationRepository
                    .findByProfessional(user.getProfessionalProfile());

            // Filtrar solo postulaciones activas (no retiradas)
            appliedJobIds = myApplications.stream()
                    .filter(app -> app.getStatus() != JobApplication.Status.WITHDRAWN)
                    .map(app -> app.getRequest().getId())
                    .collect(Collectors.toSet());
        } else {
            appliedJobIds = new HashSet<>();
        }

        // Sin filtros, devolver todo (excluyendo ofertas ya postuladas)
        if (filters == null) {
            List<ServiceRequest> filtered = jobs.stream()
                    .filter(job -> !appliedJobIds.contains(job.getId()))
                    .collect(Collectors.toList());
            return sortJobsByDate(filtered);
        }

        List<ServiceRequest> result = new ArrayList<>();

        // Buscar en cada trabajo
        for (ServiceRequest job : jobs) {
            boolean matches = true;

            // Filtro: excluir ofertas donde ya me postulé
            if (appliedJobIds.contains(job.getId())) {
                continue;
            }

            // Filtro por texto (si existe)
            if (filters.getTextoBusqueda() != null && !filters.getTextoBusqueda().trim().isEmpty()) {
                String searchText = filters.getTextoBusqueda().toLowerCase().trim();
                String title = (job.getTitle() != null) ? job.getTitle().toLowerCase() : "";
                String description = (job.getDescription() != null) ? job.getDescription().toLowerCase() : "";

                if (!title.contains(searchText) && !description.contains(searchText)) {
                    matches = false;
                }
            }

            // Filtro por categoría (si existe)
            if (matches && filters.getCategoriaId() != null) {
                if (job.getCategory() == null || !job.getCategory().getId().equals(filters.getCategoriaId())) {
                    matches = false;
                }
            }

            // Filtro por código postal (si existe)
            if (matches && filters.getCodigoPostal() != null && !filters.getCodigoPostal().trim().isEmpty()) {
                String postalCode = filters.getCodigoPostal().trim();
                if (job.getEffectiveServiceAddress() == null ||
                        !postalCode.equals(job.getEffectiveServiceAddress().getPostalCode())) {
                    matches = false;
                }
            }

            // Si pasa todos los filtros, lo añadimos
            if (matches) {
                result.add(job);
            }
        }

        return sortJobsByDate(result);
    }

    // ============================================================================
    // OBTENER SOLO TRABAJOS PUBLICADOS
    // ============================================================================
    private List<ServiceRequest> getPublishedJobs() {
        List<ServiceRequest> publishedJobs = jobRepository.findByStatus(ServiceRequest.Status.PUBLISHED);
        return publishedJobs;
    }

    // ============================================================================
    // ORDENAR TRABAJOS POR FECHA
    // ============================================================================
    private List<ServiceRequest> sortJobsByDate(List<ServiceRequest> jobs) {
        // Ordenar por fecha
        List<ServiceRequest> sortedJobs = new ArrayList<>(jobs);

        sortedJobs.sort((job1, job2) -> {
            LocalDateTime date1 = job1.getRequestedAt();
            LocalDateTime date2 = job2.getRequestedAt();

            if (date1 != null && date2 != null) {
                return date2.compareTo(date1); // Más reciente primero
            } else if (date1 != null) {
                return -1; // job1 tiene fecha, va primero
            } else if (date2 != null) {
                return 1; // job2 tiene fecha, va primero
            } else {
                return 0; // Ninguno tiene fecha
            }
        });

        return sortedJobs;
    }

}