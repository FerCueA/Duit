package es.duit.app.service.validation;

import org.springframework.stereotype.Component;

import es.duit.app.entity.AppUser;

@Component
public class UserPreconditionsValidator {

    public void requireAddress(AppUser usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }
        if (usuario.getAddress() == null) {
            throw new IllegalArgumentException("Necesitas configurar tu direccion antes de continuar");
        }
    }

    public void requireProfessionalProfile(AppUser usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuario requerido");
        }
        if (usuario.getProfessionalProfile() == null) {
            throw new IllegalArgumentException("No tienes un perfil profesional configurado");
        }
    }
}
