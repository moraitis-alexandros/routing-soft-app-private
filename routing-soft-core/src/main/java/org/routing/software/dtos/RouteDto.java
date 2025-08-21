package org.routing.software.dtos;

import lombok.*;
import org.routing.software.jpos.AssignmentJpo;
import org.routing.software.model.Assignment;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RouteDto {
    private Long truckId;
    private String truckDescription;
    private String color;
    private List<LocationNodeReadOnlyDto> stops;
    private List<List<double[]>> legCoordinates;


    public void addStop(LocationNodeReadOnlyDto locationNodeReadOnlyDto) {
        if (locationNodeReadOnlyDto == null) {
            return;
        }

        if (stops == null) {
            stops = new ArrayList<>();
        }
        stops.add(locationNodeReadOnlyDto);
    }

    public void addLegCoordinates(List<double[]> coordinates) {
        if (legCoordinates == null) legCoordinates = new ArrayList<>();
        legCoordinates.add(coordinates);
    }


}
