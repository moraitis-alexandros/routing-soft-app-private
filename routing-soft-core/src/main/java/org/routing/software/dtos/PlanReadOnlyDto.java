package org.routing.software.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlanReadOnlyDto {

    private Long id;

    private Long timeslotLength;

    private String createdAt;

    List<RouteDto> routeDtoList;

    List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList;

    List<TruckReadOnlyDto> truckReadOnlyDtoList;

    private String planStatus;

    private String algorithmSpec;

    private String uuid; // the UUID of whom the truck belongs
}


