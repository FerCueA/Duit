package es.duit.app.service;

import es.duit.app.entity.AppUser;
import es.duit.app.entity.Address;
import es.duit.app.dto.EditarPerfilDTO;
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

    public AppUser obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return authService.obtenerUsuarioAutenticado(auth);
    }

    public AppUser actualizarPerfil(EditarPerfilDTO dto) {
        AppUser user = obtenerUsuarioActual();

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