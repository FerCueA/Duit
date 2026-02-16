package es.duit.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RatingDTO {
    @NotNull(message = "El trabajo es requerido")
    private Long jobId;

    @NotNull(message = "La puntuacion es requerida")
    @Min(value = 1, message = "La puntuacion debe estar entre 1 y 5")
    @Max(value = 5, message = "La puntuacion debe estar entre 1 y 5")
    private Integer score;

    @Size(max = 1000, message = "El comentario no puede superar los 1000 caracteres")
    private String comment;
}