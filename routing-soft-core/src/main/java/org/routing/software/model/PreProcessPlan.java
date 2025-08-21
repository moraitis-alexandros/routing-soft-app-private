package org.routing.software.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.routing.software.AlgorithmSpec;
import org.routing.software.PlanStatus;
import org.routing.software.helper.Debug;
import org.routing.software.jpos.LocationNodeJpo;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.jpos.TruckJpo;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PreProcessPlan {

    private List<TruckJpo> trucksList = new ArrayList<>();
    private List<LocationNodeJpo> locationNodesList = new ArrayList<>();
    private LocationNodeJpo source;
    List<Assignment> assignments = new ArrayList<>();
    private PlanJpo initialPlan;
    private Map<Long, List<Long>> distanceMatrix = new HashMap<>(); //distance between location nodes
    private Map<Long, Map<Long, List<Long>>> truckDistanceMatrices = new HashMap<>();  //travel time between each location node for each truck
    private int DEFAULT_HOUR_VALUE = 60;
    private AlgorithmSpec algorithmSpec;
    private PlanStatus planStatus;



    public PreProcessPlan(List<TruckJpo> trucksList,
                                      List<LocationNodeJpo> locationNodesList,
                                      LocationNodeJpo source, PlanJpo initialPlan) {

        this.source = source;
        this.trucksList = trucksList;
        this.locationNodesList = locationNodesList;
        this.initialPlan = initialPlan;

        //call the  functions
        createDistanceMatrix();
        createTravelTimeMatrices();

    }

    public PreProcessPlan(List<TruckJpo> trucksList,
                          List<LocationNodeJpo> locationNodesList,
                          LocationNodeJpo source,
                          PlanJpo initialPlan,
                          Map<Long, List<Long>> distanceMatrix,
                          Map<Long, Map<Long, List<Long>>> truckDistanceMatrices,
                          int DEFAULT_HOUR_VALUE,
                          AlgorithmSpec algorithmSpec) {
        this.trucksList = trucksList;
        this.locationNodesList = locationNodesList;
        this.source = source;
        this.initialPlan = initialPlan;
        this.distanceMatrix = distanceMatrix;
        this.truckDistanceMatrices = truckDistanceMatrices;
        this.DEFAULT_HOUR_VALUE = DEFAULT_HOUR_VALUE;
        this.algorithmSpec = algorithmSpec;
    }

    /**
     * A distance matrix from - to all LocationNodes (node 0 included)
     */
    private void createDistanceMatrix() {
        for (LocationNodeJpo sourceNode : locationNodesList) {
            List<Long> distances = locationNodesList.stream()
                    .map(targetNode -> {
                        double distance = Math.sqrt(
                                Math.pow(sourceNode.getCoordinatesX() - targetNode.getCoordinatesX(), 2)
                                        + Math.pow(sourceNode.getCoordinatesY() - targetNode.getCoordinatesY(), 2)
                        );
                        return (long) distance;
                    })
                    .collect(Collectors.toList());

            distanceMatrix.put(sourceNode.getId(), distances);
        }
    }
    

    /**
     * A function to create a nxn matrix for all trucks travel.
     * Each truck should have its own matrix due its unique max speed
     */
    private void createTravelTimeMatrices() {
        for (TruckJpo truck : trucksList) {

            // Create a new matrix for each truck
            Map<Long, List<Long>> truckMatrix = new HashMap<>();

            //for each tank calculate the travel time keeping in mind truck max speed and plan defined timeslot
            for (LocationNodeJpo sourceNode : locationNodesList) {

                List<Long> travelDurationInTimeslots = distanceMatrix
                        .get(sourceNode.getId())
                        .stream()
                        .map(c ->
                                (c*DEFAULT_HOUR_VALUE/truck.getMaxSpeed())/initialPlan.getTimeslotLength())
                        .collect(Collectors.toList());

                truckMatrix.put(sourceNode.getId(), travelDurationInTimeslots);
            }
            truckDistanceMatrices.put(truck.getId(), truckMatrix);
        }
    }

}
