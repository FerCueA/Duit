package es.duit.app.service.util;

import java.util.Locale;

import org.springframework.stereotype.Component;

// ============================================================================
// UTILIDAD DE NORMALIZACION DE IDENTIDAD
// ============================================================================
@Component
public class IdentityNormalizer {

    public String normalizeEmail(String email) {
        if (email == null) {
            return "";
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public String normalizeDni(String dni) {
        if (dni == null) {
            return "";
        }
        return dni.trim().toUpperCase(Locale.ROOT);
    }
}