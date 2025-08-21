package org.routing.software.model;

import jakarta.enterprise.context.RequestScoped;
import lombok.AllArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.dtos.PlanReadOnlyDto;
import org.routing.software.dtos.RouteDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.service.IMapClient;
import org.routing.software.service.OSMRClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RequestScoped
@AllArgsConstructor
public class MultipleVrpAlgorithm {

    Map<LocationNodeReadOnlyDto, Map<LocationNodeReadOnlyDto, JSONObject>> objectMatrixFromOpenApi;
    Map<LocationNodeReadOnlyDto, Map<LocationNodeReadOnlyDto, Long>> durationMatrixFromOpenApi;
    Map<LocationNodeReadOnlyDto, Map<LocationNodeReadOnlyDto, Long>> processDurationMatrixFromOpenApi;

    IMapClient mapClient;

    public MultipleVrpAlgorithm() {
        objectMatrixFromOpenApi = new HashMap<>();
        durationMatrixFromOpenApi = new HashMap<>();
        processDurationMatrixFromOpenApi = new HashMap<>();
        mapClient = new OSMRClient();
    }

    public PlanReadOnlyDto solve(PlanReadOnlyDto planReadOnlyDto) {

;

        List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList = planReadOnlyDto.getLocationNodeReadOnlyDtoList();
        List<TruckReadOnlyDto> truckReadOnlyDtoList = planReadOnlyDto.getTruckReadOnlyDtoList();

        //start populating each
        locationNodeReadOnlyDtoList
                .forEach(node -> {

                    objectMatrixFromOpenApi.put(node, new HashMap<>());
                    durationMatrixFromOpenApi.put(node, new HashMap<>());
                });

       objectMatrixFromOpenApi.entrySet().forEach(nodeInMap -> {

               locationNodeReadOnlyDtoList.forEach(nodeInList -> {
                   try {
                       JSONObject jsonObject = mapClient.fetchRouteObject(String.valueOf(nodeInMap.getKey().getCoordinatesX()),
                               String.valueOf(nodeInMap.getKey().getCoordinatesY()), String.valueOf(nodeInList.getCoordinatesX()),
                               String.valueOf(nodeInList.getCoordinatesY()));
                       Map<LocationNodeReadOnlyDto, JSONObject> nodeToGo = objectMatrixFromOpenApi.get(nodeInMap.getKey());
                       nodeToGo.put(nodeInList, jsonObject);

                       //populate the distanceMatrix
                       Map<LocationNodeReadOnlyDto, Long> nodeDistance = durationMatrixFromOpenApi.get(nodeInMap.getKey());
                       JSONObject route = jsonObject.getJSONArray("routes").getJSONObject(0);
                       double duration = route.getDouble("duration");
                       nodeDistance.put(nodeInList, (long)duration);

                   } catch (IOException | InterruptedException e) {
                       throw new RuntimeException(e);
                   }
               });
       });
        printMatrixResult(locationNodeReadOnlyDtoList);

      //calculate total demand
        AtomicLong totalDemand = new AtomicLong();
        locationNodeReadOnlyDtoList.forEach(node -> totalDemand.addAndGet(node.getCapacity()));

        //calculate total capacity
        AtomicLong totalCapacity = new AtomicLong();
        truckReadOnlyDtoList.forEach(truck -> totalCapacity.addAndGet(truck.getCapacity()));

        int trucksToBeInvolved = truckReadOnlyDtoList.size();
        int currentTrucksInvolved = 0;

        List<LocationNodeReadOnlyDto> sourceList = durationMatrixFromOpenApi.entrySet().stream().filter(
                locationNodeReadOnlyDtoMapEntry ->
                        locationNodeReadOnlyDtoMapEntry.getKey().isSource())
                .map(locationNodeReadOnlyDtoMapEntry ->
                        locationNodeReadOnlyDtoMapEntry.getKey())
                .collect(Collectors.toList());


        if (sourceList.size() != 1) {
            System.out.println("Source should be one");
        }

        int i = 0;
        LocationNodeReadOnlyDto source = sourceList.get(0);

        //create a deep copy
        processDurationMatrixFromOpenApi = durationMatrixFromOpenApi.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> new HashMap<>(e.getValue())
                ));


        List<RouteDto> routeDtoList = new ArrayList<>();
        PlanReadOnlyDto planCompleted = new PlanReadOnlyDto();
        planCompleted.setId(planReadOnlyDto.getId());
        planCompleted.setUuid(planReadOnlyDto.getUuid());
        planCompleted.setTruckReadOnlyDtoList(planReadOnlyDto.getTruckReadOnlyDtoList());
        planCompleted.setLocationNodeReadOnlyDtoList(planReadOnlyDto.getLocationNodeReadOnlyDtoList());
        planCompleted.setAlgorithmSpec(planReadOnlyDto.getAlgorithmSpec());

        while (currentTrucksInvolved < trucksToBeInvolved) {
            System.out.println("===== Truck " + currentTrucksInvolved + " =====");
            TruckReadOnlyDto pickedTruck = truckReadOnlyDtoList.get(currentTrucksInvolved);
            long currentTruckCapacityOccupated = 0;
            long totalTruckCapacity = pickedTruck.getCapacity();

            LocationNodeReadOnlyDto nodeFrom = source;
            RouteDto routeDto = new RouteDto();
            while (currentTruckCapacityOccupated <= totalTruckCapacity) {

                routeDto.setTruckDescription(pickedTruck.getDescription());
                routeDto.setTruckId(pickedTruck.getId());

                //find the closest node to A

                System.out.println("Iteration " + i);
                System.out.println("nodeFrom " + nodeFrom.getDescription());
                LocationNodeReadOnlyDto nodeTo = findNearestNode(nodeFrom);



                currentTruckCapacityOccupated += nodeTo.getCapacity();

                if (currentTruckCapacityOccupated > totalTruckCapacity) {

                }

                if (nodeTo == null) {
                    break; //when all node finish
                } else {
                    System.out.println("nodeTo " + nodeTo.getDescription());
                }


                routeDto.addStop(nodeTo);
                currentTruckCapacityOccupated += nodeTo.getCapacity();
                nodeFrom = nodeTo;
                i++;
            }
            routeDtoList.add(routeDto);
            currentTrucksInvolved++;


        }
        for (RouteDto route : routeDtoList) {
            if (route.getStops().isEmpty() || !route.getStops().get(route.getStops().size() - 1).isSource()) {
                route.addStop(source);
            }
        }

        planCompleted.setRouteDtoList(routeDtoList);
        populateLegCoordinates(routeDtoList, source); //create the paths suitable for front end

        planCompleted.getRouteDtoList().forEach(route -> {
            System.out.println("Truck " + route.getTruckDescription());
            System.out.println("==STOPS==");
            route.getStops().forEach(stop -> System.out.println("  " + stop.getDescription()));

            System.out.println("==LEG COORDINATES==");
            List<List<double[]>> legs = route.getLegCoordinates();
            if (legs != null) {
                // Create a list of stops including the source at the end
                List<LocationNodeReadOnlyDto> stopsWithSource = new ArrayList<>(route.getStops());
                stopsWithSource.add(source); // match what populateLegCoordinates did

                for (int j = 0; j < legs.size(); j++) {
                    List<double[]> leg = legs.get(j);
                    System.out.println("Leg " + (j + 1) + " (from " +
                            stopsWithSource.get(j).getDescription() +
                            " to " + stopsWithSource.get(j + 1).getDescription() + "):");
                    leg.stream().limit(3).forEach(coord ->
                            System.out.println("    [" + coord[0] + ", " + coord[1] + "]")
                    );
                    if (leg.size() > 3) System.out.println("    ... (" + leg.size() + " total points)");
                }
            } else {
                System.out.println("No leg coordinates found!");
            }
        });
        System.out.println("PLAN OK");
        return planCompleted;

        //add source at start and end of each route
        //create the polygons based on map api

    }


    private LocationNodeReadOnlyDto findNearestNode(LocationNodeReadOnlyDto from) {

        Map<LocationNodeReadOnlyDto, Long> pickedNode = processDurationMatrixFromOpenApi.get(from);
        Optional<Map.Entry<LocationNodeReadOnlyDto, Long>> nearestEntry = pickedNode.entrySet()
                .stream()
                .filter(entry -> !entry.getKey().getUuid().equals(from.getUuid())) // skip current node
                .min(Comparator.comparingLong(Map.Entry::getValue));
        LocationNodeReadOnlyDto nearestNode = nearestEntry.map(Map.Entry::getKey).orElse(null);

        //remove the node from all locationList
        Map<LocationNodeReadOnlyDto, Map<LocationNodeReadOnlyDto, Long>> filteredMap =
                processDurationMatrixFromOpenApi.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                es -> es.getValue().entrySet().stream()
                                        .filter(entry -> !entry.getKey().getUuid().equals(from.getUuid()))
                                        .collect(Collectors.toMap(
                                                Map.Entry::getKey,
                                                Map.Entry::getValue
                                        ))
                        ));


        //remove the node from process list
        if (!from.isSource()) {
            processDurationMatrixFromOpenApi = filteredMap;
            processDurationMatrixFromOpenApi.remove(from);
            System.out.println("Removed " + from.getDescription());
        }

        if (nearestNode.isSource() && processDurationMatrixFromOpenApi.size() == 1) {
            return null;
        }

        return nearestNode;
    }


    private void printMatrixResult(List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList) {
        // ✅ Print readable matrix
        System.out.println("\n=== Duration Matrix (seconds) ===");
        System.out.print("From/To\t");
        locationNodeReadOnlyDtoList.forEach(to -> System.out.print(to.getDescription() + "\t"));
        System.out.println();

        locationNodeReadOnlyDtoList.forEach(from -> {
            System.out.print(from.getDescription() + "\t");
            locationNodeReadOnlyDtoList.forEach(to -> {
                Long val = durationMatrixFromOpenApi.get(from).get(to);
                System.out.print((val != null ? val : "X") + "\t");
            });
            System.out.println();
        });
    }

    private void populateLegCoordinates(List<RouteDto> routeDtoList, LocationNodeReadOnlyDto source) {
        for (RouteDto route : routeDtoList) {
            List<LocationNodeReadOnlyDto> stops = new ArrayList<>(route.getStops());

            // Add source at the end for round trip
            stops.add(source);      // end at source

            for (int i = 0; i < stops.size() - 1; i++) {
                LocationNodeReadOnlyDto from = stops.get(i);
                LocationNodeReadOnlyDto to = stops.get(i + 1);

                JSONObject routeJson = objectMatrixFromOpenApi.get(from).get(to);
                JSONArray coordinatesArray = routeJson.getJSONArray("routes")
                        .getJSONObject(0)
                        .getJSONObject("geometry")
                        .getJSONArray("coordinates");

                List<double[]> legCoordinates = new ArrayList<>();
                for (int k = 0; k < coordinatesArray.length(); k++) {
                    JSONArray pair = coordinatesArray.getJSONArray(k);
                    // OSRM gives [lon, lat], Leaflet expects [lat, lon]
                    legCoordinates.add(new double[]{ pair.getDouble(1), pair.getDouble(0) });
                }

                route.addLegCoordinates(legCoordinates);
            }
        }
    }

}
