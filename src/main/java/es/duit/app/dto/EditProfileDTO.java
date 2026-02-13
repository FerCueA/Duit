package es.duit.app.dto;

import java.math.BigDecimal;

import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import es.duit.app.entity.ProfessionalProfile;
import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EditProfileDTO {

        private static final String DEFAULT_COUNTRY = "España";

        public interface UserProfileGroup {
        }

        public interface ProfessionalProfileGroup {
        }

        @NotBlank(message = "El nombre es obligatorio", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String firstName;

        @NotBlank(message = "El apellido es obligatorio", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(min = 2, max = 150, message = "El apellido debe tener entre 2 y 150 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String lastName;

        @Pattern(regexp = "^(\\+34[0-9]{9})?$", message = "El teléfono debe ser español: +34 seguido de 9 dígitos", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String phone;

        @NotBlank(message = "La dirección es obligatoria", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String address;

        @NotBlank(message = "La ciudad es obligatoria", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String city;

        @Pattern(regexp = "^([0-9]{5})?$", message = "El código postal debe tener 5 dígitos", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        private String postalCode;

        @NotBlank(message = "La provincia es obligatoria", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(min = 2, max = 100, message = "La provincia debe tener entre 2 y 100 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        public String province;

        @NotBlank(message = "El país es obligatorio", groups = { UserProfileGroup.class,
                        ProfessionalProfileGroup.class })
        @Size(max = 100, message = "El país no puede exceder los 100 caracteres", groups = {
                        UserProfileGroup.class, ProfessionalProfileGroup.class })
        public String country;

        @NotNull(message = "La tarifa por hora es obligatoria", groups = ProfessionalProfileGroup.class)
        @DecimalMin(value = "5.00", message = "La tarifa por hora mínima es 5€", groups = ProfessionalProfileGroup.class)
        @DecimalMax(value = "500.00", message = "La tarifa por hora máxima es 500€", groups = ProfessionalProfileGroup.class)
        @Column(name = "hourly_rate", precision = 8, scale = 2, nullable = false)
        private BigDecimal hourlyRate;

        @NotBlank(message = "El NIF es obligatorio para profesionales", groups = ProfessionalProfileGroup.class)
        @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "El NIF debe tener el formato correcto (8 dígitos + letra)", groups = ProfessionalProfileGroup.class)
        @Column(name = "nif", length = 9, unique = true, nullable = false)
        private String nif;

        @NotBlank(message = "La descripción profesional es obligatoria", groups = ProfessionalProfileGroup.class)
        @Size(min = 50, max = 2000, message = "La descripción debe tener entre 50 y 2000 caracteres", groups = ProfessionalProfileGroup.class)
        private String description;

        public EditProfileDTO() {
                this.country = DEFAULT_COUNTRY;
        }

        public EditProfileDTO(AppUser user) {
                if (user != null) {
                        this.firstName = user.getFirstName();
                        this.lastName = user.getLastName();
                        this.phone = user.getPhone();
                        this.nif = user.getDni();
                        copyAddress(user.getAddress());
                        copyProfessionalProfile(user.getProfessionalProfile());
                }
        }

        private void copyAddress(Address addr) {
                if (addr != null) {
                        this.address = addr.getAddress();
                        this.city = addr.getCity();
                        this.postalCode = addr.getPostalCode();
                        this.province = addr.getProvince();
                        this.country = (addr.getCountry() == null || addr.getCountry().trim().isEmpty())
                                        ? DEFAULT_COUNTRY
                                        : addr.getCountry();
                } else {
                        this.country = DEFAULT_COUNTRY;
                }
        }

        private void copyProfessionalProfile(ProfessionalProfile hour) {
                if (hour != null) {
                        this.hourlyRate = hour.getHourlyRate();
                        if (hour.getNif() != null && !hour.getNif().trim().isEmpty()) {
                                this.nif = hour.getNif();
                        }
                        this.description = hour.getDescription();
                }
        }

}
