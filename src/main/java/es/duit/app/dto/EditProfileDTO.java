package es.duit.app.dto;

import es.duit.app.entity.Address;
import es.duit.app.entity.AppUser;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EditProfileDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 150, message = "El apellido no puede exceder los 150 caracteres")
    private String lastName;

    @Pattern(regexp = "^\\+34[0-9]{9}$", message = "El teléfono debe ser español: +34 seguido de 9 dígitos")
    private String phone;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres")
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres")
    private String city;

    @Pattern(regexp = "^[0-9]{5}$", message = "El código postal debe tener 5 dígitos")
    private String postalCode;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(min = 2, max = 100, message = "La provincia debe tener entre 2 y 100 caracteres")
    public String province;

    @NotBlank(message = "El país es obligatorio")
    public String country;

    public EditProfileDTO() {
    }

    public EditProfileDTO(AppUser user) {
        if (user != null) {
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.phone = user.getPhone();
            copyAddress(user.getAddress());
        }
    }

    private void copyAddress(Address addr) {
        if (addr != null) {
            this.address = addr.getAddress();
            this.city = addr.getCity();
            this.postalCode = addr.getPostalCode();
            this.province = addr.getProvince();
            this.country = addr.getCountry();
        }
    }

}
