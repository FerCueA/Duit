package es.duit.app.controller;

import es.duit.app.dto.RequestDTO;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.ServiceRequest;
import es.duit.app.service.AuthService;
import es.duit.app.service.RequestService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

// ============================================================================
// CONTROLADOR DE FORMULARIO DE SOLICITUDES - GESTIONA CREACIÓN Y EDICIÓN
// ============================================================================
@Controller
@RequestMapping("/requests")
public class RequestFormController {

    private final RequestService serviceRequestService;
    private final AuthService authService;

    public RequestFormController(RequestService serviceRequestService, AuthService authService) {
        this.serviceRequestService = serviceRequestService;
        this.authService = authService;
    }

    // ============================================================================
    // INICIALIZA EL FORMULARIO CON VALORES POR DEFECTO
    // ============================================================================
    @ModelAttribute("form")
    public RequestDTO initializeForm() {
        RequestDTO formulario = new RequestDTO();
        return formulario;
    }

    // ============================================================================
    // MUESTRA EL FORMULARIO DE SOLICITUDES (CREAR O EDITAR)
    // ============================================================================
    @GetMapping("/request")
    public String showRequestForm(@ModelAttribute("form") RequestDTO form,
            Authentication auth,
            Model model,
            @RequestParam(required = false) Long edit,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario actualmente logueado
            AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

            // Configurar datos básicos del formulario
            basicFormData(model, usuarioLogueado, form);

            // Verificar si estamos editando una solicitud existente
            boolean esEdicion = edit != null;
            if (esEdicion) {
                formForEdit(edit, usuarioLogueado, form, model);
            } else {
                formForNewRequest(form);
            }

            // Enviar formulario a la vista
            model.addAttribute("form", form);
            return "jobs/request";

        } catch (IllegalArgumentException error) {
            // Si hay error, redirigir con mensaje de error
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/requests/my-requests";
        }
    }

    // ============================================================================
    // PROCESA EL ENVÍO DEL FORMULARIO DE SOLICITUD
    // ============================================================================
    @PostMapping("/request")
    public String submitRequestForm(@Valid @ModelAttribute("form") RequestDTO form,
            BindingResult bindingResult,
            Authentication auth,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Obtener usuario logueado
        AppUser usuarioLogueado = authService.getAuthenticatedUser(auth);

        try {
            // Validar que no hay errores en el formulario
            if (bindingResult.hasErrors()) {
                // Manejar errores de validación - volver al formulario con errores y datos
                // necesarios
                model.addAttribute("habitualAddress", usuarioLogueado.getAddress());
                model.addAttribute("categorias", serviceRequestService.getActiveCategories());

                boolean esEdicion = form.getEditId() != null;
                model.addAttribute("modoEdicion", esEdicion);
                return "jobs/request";
            }

            // Guardar la solicitud en la base de datos
            serviceRequestService.saveRequest(form, usuarioLogueado);

            // Preparar mensaje de éxito según si fue crear o editar
            boolean fueEdicion = form.getEditId() != null;
            String mensajeExito = fueEdicion ? "Solicitud actualizada correctamente" : "Solicitud creada correctamente";
            redirectAttributes.addFlashAttribute("success", mensajeExito);
            return "redirect:/requests/my-requests";

        } catch (IllegalArgumentException error) {
            // Manejar errores de validación de negocio
            bindingResult.reject("error.global", error.getMessage());

            // Volver al formulario con datos necesarios
            model.addAttribute("habitualAddress", usuarioLogueado.getAddress());
            model.addAttribute("categorias", serviceRequestService.getActiveCategories());

            boolean esEdicion = form.getEditId() != null;
            model.addAttribute("modoEdicion", esEdicion);

            return "jobs/request";

        } catch (Exception error) {
            // Error inesperado: redirigir con mensaje genérico
            redirectAttributes.addFlashAttribute("error", "Error inesperado. Inténtalo de nuevo.");
            return "redirect:/requests/request";
        }
    }

    // ============================================================================
    // CONFIGURA LOS DATOS BÁSICOS DEL FORMULARIO
    // ============================================================================
    private void basicFormData(Model model, AppUser usuario, RequestDTO form) {
        // Enviar datos comunes necesarios para el formulario
        model.addAttribute("habitualAddress", usuario.getAddress());
        model.addAttribute("categorias", serviceRequestService.getActiveCategories());

        // Configurar valores por defecto
        form.setAddressOption("habitual");
        form.setCountry("España");
    }

    // ============================================================================
    // CONFIGURA EL FORMULARIO PARA EDITAR UNA SOLICITUD EXISTENTE
    // ============================================================================
    private void formForEdit(Long editId, AppUser usuario, RequestDTO form, Model model) {
        // Buscar la solicitud que queremos editar
        ServiceRequest solicitudExistente = serviceRequestService.getUserRequest(editId, usuario);

        // Copiar datos básicos al formulario
        copyBasicRequestData(solicitudExistente, form);

        // Copiar fecha límite si existe
        copyDeadline(solicitudExistente, form);

        // Copiar información de dirección
        copyServiceAddress(solicitudExistente, form);

        // Indicar que estamos en modo edición
        model.addAttribute("modoEdicion", true);
    }

    // ============================================================================
    // CONFIGURA EL FORMULARIO PARA UNA NUEVA SOLICITUD
    // ============================================================================
    private void formForNewRequest(RequestDTO form) {
        // Establecer valores por defecto para nueva solicitud
        form.setAddressOption("habitual");
        form.setCountry("España");
    }

    // ============================================================================
    // COPIA DATOS BÁSICOS DE LA SOLICITUD AL FORMULARIO
    // ============================================================================
    private void copyBasicRequestData(ServiceRequest solicitud, RequestDTO form) {
        form.setEditId(solicitud.getId());
        form.setTitle(solicitud.getTitle());
        form.setDescription(solicitud.getDescription());
        form.setCategoryId(solicitud.getCategory().getId());
    }

    // ============================================================================
    // COPIA LA FECHA LÍMITE DE LA SOLICITUD AL FORMULARIO
    // ============================================================================
    private void copyDeadline(ServiceRequest solicitud, RequestDTO form) {
        // Verificar si la solicitud tiene fecha límite
        boolean tieneFechaLimite = solicitud.getDeadline() != null;
        if (tieneFechaLimite) {
            form.setDeadline(solicitud.getDeadline().toLocalDate());
        }
    }

    // ============================================================================
    // COPIA LA DIRECCIÓN DE SERVICIO DE LA SOLICITUD AL FORMULARIO
    // ============================================================================
    private void copyServiceAddress(ServiceRequest solicitud, RequestDTO form) {
        // Verificar si tiene dirección específica o usa la habitual
        boolean tienesDireccionEspecifica = solicitud.hasSpecificServiceAddress();

        if (tienesDireccionEspecifica) {
            // Dirección específica nueva
            form.setAddressOption("new");
            form.setAddress(solicitud.getServiceAddress().getAddress());
            form.setCity(solicitud.getServiceAddress().getCity());
            form.setPostalCode(solicitud.getServiceAddress().getPostalCode());
            form.setProvince(solicitud.getServiceAddress().getProvince());
            form.setCountry(solicitud.getServiceAddress().getCountry());
        } else {
            // Usa dirección habitual del usuario
            form.setAddressOption("habitual");
        }
    }

}
