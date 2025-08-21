package org.routing.software.dtos;


import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class PlanInsertDto {

    @NotEmpty(message = "The plan should have at least one truck")
    List<TruckReadOnlyDto> trucksList;

    @NotEmpty(message = "The plan should have at least one location node")
    List<LocationNodeReadOnlyDto> locationNodeList;

    String algorithmSpec;

    private Long timeslotLength;
}
