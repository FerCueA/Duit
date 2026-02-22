package es.duit.app.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.duit.app.dto.EditProfileDTO;
import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.ProfessionalProfile;
import es.duit.app.repository.AddressRepository;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.ProfessionalProfileRepository;
import lombok.RequiredArgsConstructor;

// ============================================================================
// SERVICIO PARA GESTIONAR OPERACIONES CON USUARIOS (PERFILES, AUTENTICACIÓN)
// ============================================================================
@Service
@Transactional
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AddressRepository addressRepository;
    private final ProfessionalProfileRepository professionalProfileRepository;
    private final AuthService authService;

    // ============================================================================
    // ACTUALIZA EL PERFIL DEL USUARIO CON NUEVOS DATOS
    // ============================================================================
    public AppUser updateUserProfile(EditProfileDTO dto) {
        // Obtener el usuario actualmente logueado
        AppUser user = getCurrentUser();

        if (dto == null) {
            throw new IllegalArgumentException("Datos de perfil requeridos");
        }

        // Actualizar datos personales
        user.setFirstName(dto.getFirstName().trim());
        user.setLastName(dto.getLastName().trim());
        user.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);

        // Obtener o crear la dirección
        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }

        // Actualizar datos de la dirección
        address.setAddress(dto.getAddress().trim());
        address.setCity(dto.getCity().trim());
        address.setPostalCode(dto.getPostalCode() != null ? dto.getPostalCode().trim() : null);
        address.setProvince(dto.getProvince().trim());
        address.setCountry("España");

        // Guardar dirección en la BD
        addressRepository.save(address);

        // Guardar usuario y retornar
        return appUserRepository.save(user);
    }

    // ============================================================================
    // ACTUALIZA EL PERFIL DEL USUARIO CON NUEVOS DATOS
    // ============================================================================

    public AppUser updateProfessionalProfile(EditProfileDTO dto) {
        // Obtener el usuario actualmente logueado
        AppUser userPro = getCurrentUser();
        if (dto == null) {
            throw new IllegalArgumentException("Datos de perfil requeridos");
        }

        // Actualizar datos personales
        userPro.setFirstName(dto.getFirstName().trim());
        userPro.setLastName(dto.getLastName().trim());
        userPro.setPhone(dto.getPhone() != null ? dto.getPhone().trim() : null);

        // Obtener o crear la dirección
        Address address = userPro.getAddress();

        if (address == null) {
            address = new Address();
            userPro.setAddress(address);
        }

        // Actualizar datos de la dirección
        address.setAddress(dto.getAddress().trim());
        address.setCity(dto.getCity().trim());
        address.setPostalCode(dto.getPostalCode() != null ? dto.getPostalCode().trim() : null);
        address.setProvince(dto.getProvince().trim());
        address.setCountry("España");

        // Guardar dirección en la BD
        addressRepository.save(address);

        // Obtener o crear el perfil profesional

        ProfessionalProfile profesional = userPro.getProfessionalProfile();

        if (profesional == null) {
            profesional = new ProfessionalProfile();
            profesional.setUser(userPro);
            userPro.setProfessionalProfile(profesional);
        }
        // Actualizar datos del perfil profesional
        profesional.setHourlyRate(dto.getHourlyRate());
        String nif = userPro.getDni();
        if (nif == null || nif.trim().isEmpty()) {
            nif = dto.getNif() != null ? dto.getNif().trim() : null;
        }
        profesional.setNif(nif);
        profesional.setDescription(dto.getDescription().trim());

        // Guardar perfil profesional en la BD
        professionalProfileRepository.save(profesional);

        // Guardar usuario y retornar
        return appUserRepository.save(userPro);
    }

    // ============================================================================
    // OBTIENE EL USUARIO ACTUALMENTE LOGUEADO
    // ============================================================================
    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.getAuthenticatedUser(auth);
    }

    // ============================================================================
    // DESACTIVA LA CUENTA DEL USUARIO ACTUAL
    // ============================================================================
    public void deactivateCurrentUser() {
        AppUser user = getCurrentUser();

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("No se pudo identificar la cuenta a eliminar");
        }

        user.setActive(false);
        appUserRepository.save(user);
    }
}