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

    public AppUser getActualUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.obtenerUsuarioAutenticado(auth);
    }

    public AppUser updateUserProfile(EditProfileDTO dto) {
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (dto.getLastName() == null || dto.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (dto.getAddress() == null || dto.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }

        if (dto.getCity() == null || dto.getCity().trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad es obligatoria.");
        }

        if (dto.getProvince() == null || dto.getProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("La provincia es obligatoria.");
        }

        if (dto.getCountry() == null || dto.getCountry().trim().isEmpty()) {
            throw new IllegalArgumentException("El país es obligatorio.");
        }

        if (dto.getPhone() != null && !dto.getPhone().trim().isEmpty()) {
            String phonePattern = "^\\+34[0-9]{9}$";
            if (!dto.getPhone().matches(phonePattern)) {
                throw new IllegalArgumentException("El teléfono debe ser español: +34 seguido de 9 dígitos.");
            }
        }

        if (dto.getPostalCode() != null && !dto.getPostalCode().trim().isEmpty()) {
            String postalCodePattern = "^[0-9]{5}$";
            if (!dto.getPostalCode().matches(postalCodePattern)) {
                throw new IllegalArgumentException("El código postal debe tener 5 dígitos.");
            }
        }

        if (dto.getFirstName().length() < 2 || dto.getFirstName().length() > 150) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 150 caracteres.");
        }

        if (dto.getLastName().length() > 150) {
            throw new IllegalArgumentException("El apellido no puede exceder los 150 caracteres.");
        }

        AppUser user = getActualUser();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());

        Address address = user.getAddress();

        if (address == null) {
            address = new Address();
            user.setAddress(address);
        }

        address.setAddress(dto.getAddress());
        address.setCity(dto.getCity());
        address.setPostalCode(dto.getPostalCode());
        address.setProvince(dto.getProvince());
        address.setCountry(dto.getCountry());

        addressRepository.save(address);

        return appUserRepository.save(user);
    }
}