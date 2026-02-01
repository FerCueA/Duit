package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Address;
import es.duit.app.dto.EditProfileDTO;
import es.duit.app.repository.AppUserRepository;
import es.duit.app.repository.AddressRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// ============================================================================
// SERVICIO PARA GESTIONAR OPERACIONES CON USUARIOS (PERFILES, AUTENTICACIÓN)
// ============================================================================
@Service
@Transactional
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AddressRepository addressRepository;
    private final AuthService authService;

    public AppUserService(AppUserRepository appUserRepository,
            AddressRepository addressRepository,
            AuthService authService) {
        this.appUserRepository = appUserRepository;
        this.addressRepository = addressRepository;
        this.authService = authService;
    }

    // ============================================================================
    // OBTIENE EL USUARIO ACTUALMENTE LOGUEADO
    // ============================================================================
    public AppUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.getAuthenticatedUser(auth);
    }

    // ============================================================================
    // ACTUALIZA EL PERFIL DEL USUARIO CON NUEVOS DATOS
    // ============================================================================
    public AppUser updateUserProfile(EditProfileDTO dto) {
        // Obtener el usuario actualmente logueado
        AppUser user = getCurrentUser();

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
        address.setCountry(dto.getCountry().trim());

        // Guardar dirección en la BD
        addressRepository.save(address);

        // Guardar usuario y retornar
        return appUserRepository.save(user);
    }
}