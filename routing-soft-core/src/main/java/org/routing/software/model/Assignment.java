package org.routing.software.model;


import lombok.*;
import org.routing.software.jpos.IdentifiableEntity;
import java.util.List;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Assignment implements IdentifiableEntity {

    private Long id;

    private Long sequence;

    private List<LocationNode> locationNodeList; //Based on the algorithm a Truck may visit multiple location nodes on a certain assignment

    private Truck truck;

    private List<Integer> locationNodeListVisitTimeslots; //each Location node will be aligned with a certain visit timeslot

    private Integer sourceLoadingTimeslot;

    private Integer sourceLoadingSpot; //A Truck will load in source, which may have different spots (one or more);

}