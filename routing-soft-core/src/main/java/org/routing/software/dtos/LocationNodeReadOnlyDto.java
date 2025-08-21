package org.routing.software.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LocationNodeReadOnlyDto {

    private Long id;

    @JsonProperty("isSource")
    private boolean isSource;

    private double coordinatesX; //TODO check to remove if needed because we have the matrix

    private double coordinatesY; //TODO check to remove if needed because we have the matrix

    private Long capacity;

    private String description;

    private String uuid; // the UUID of whom the truck belongs

}

