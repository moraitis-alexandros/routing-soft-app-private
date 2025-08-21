package org.routing.software.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LocationNodeInsertDto {

    @JsonProperty("isSource") //by default, jackson makes puts 'is' if boolean so it cannot find it without annotation
    private boolean isSource;

    private double coordinatesX; //TODO check to remove if needed because we have the matrix

    private double coordinatesY; //TODO check to remove if needed because we have the matrix

    private Long capacity;

    private String description;

}

