package org.routing.software.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TruckReadOnlyDto {

    private Long id;

    private Long unloadingTime;

    private Long maxSpeed;

    private Long capacity;

    private String description;

    private String uuid; // the UUID of whom the truck belongs
}

