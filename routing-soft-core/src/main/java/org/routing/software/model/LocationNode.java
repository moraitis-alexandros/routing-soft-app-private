package org.routing.software.model;

import lombok.*;
import org.routing.software.jpos.IdentifiableEntity;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LocationNode {

    private Long id;

    private boolean isSource;

    private double coordinatesX; //TODO check to remove if needed because we have the matrix

    private double coordinatesY; //TODO check to remove if needed because we have the matrix

    private Long capacity;

    private String description;

    private String uuid; //the owner of the truck (user uuid) TODO check if need to remove

}
