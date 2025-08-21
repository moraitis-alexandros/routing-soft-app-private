package org.routing.software.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TruckInsertDto {

    @NotNull(message = "unLoadingTime is obligatory")
    @Min(value = 0, message = "unloadingTime must be >= 0")
    @Max(value = 60, message = "unloadingTime must be <= 60 minutes")
    private Long unloadingTime;

    @NotNull(message = "maxSpeed is obligatory")
    @Min(value = 0, message = "maxSpeed must be >= 0")
    @Max(value = 100, message = "maxSpeed must be <= 100 km/h")
    private Long maxSpeed;

    @NotNull(message = "capacity is obligatory")
    @Min(value = 0, message = "capacity must be >= 0")
    @Max(value = 40000, message = "capacity should be <= 40,000")
    private Long capacity;

    @NotNull(message = "description is obligatory")
    @Size(min = 5, max = 255, message = "description must be between 5-255 chars")
    private String description;

}
