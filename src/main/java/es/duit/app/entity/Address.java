package es.duit.app.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "address")
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_address")
    private Long id;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 200, message = "La dirección debe tener entre 5 y 200 caracteres")
    @Column(name = "street_address", length = 200, nullable = false)
    private String address;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(min = 2, max = 100, message = "La ciudad debe tener entre 2 y 100 caracteres")
    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Size(max = 10, message = "El código postal no puede exceder los 10 caracteres")
    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @NotBlank(message = "La provincia es obligatoria")
    @Size(min = 2, max = 100, message = "La provincia debe tener entre 2 y 100 caracteres")
    @Column(name = "province", length = 100, nullable = false)
    private String province;

    @NotBlank(message = "El país es obligatorio")
    @Size(min = 2, max = 50, message = "El país debe tener entre 2 y 50 caracteres")
    @Column(name = "country", length = 50, nullable = false)
    private String country = "España";

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private List<AppUser> users = new ArrayList<>();

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "serviceAddress", fetch = FetchType.LAZY)
    private List<ServiceRequest> serviceRequests = new ArrayList<>();

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder(address);
        if (city != null) {
            fullAddress.append(", ").append(city);
        }
        if (postalCode != null) {
            fullAddress.append(" ").append(postalCode);
        }
        if (province != null) {
            fullAddress.append(", ").append(province);
        }
        if (country != null) {
            fullAddress.append(", ").append(country);
        }
        return fullAddress.toString();
    }
}
