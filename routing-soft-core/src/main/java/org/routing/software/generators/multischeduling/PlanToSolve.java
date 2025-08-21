package org.routing.software.generators.multischeduling;

import lombok.*;
import org.routing.software.AlgorithmSpec;
import org.routing.software.helper.Debug;
import org.routing.software.jpos.LocationNodeJpo;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.jpos.TruckJpo;
import org.routing.software.model.PreProcessPlan;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A wrapper class for Preprocess Plan that is used by the certain generator
 * Here it will be added attributes tha are needed only for this certain generator
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PlanToSolve extends PreProcessPlan {

    private Map<Long, List<Long>> distanceMatrixForJobScheduling = new HashMap<>();

    public PlanToSolve(List<TruckJpo> trucksList,
                       List<LocationNodeJpo> locationNodesList,
                       LocationNodeJpo source,
                       PlanJpo initialPlan,
                       Map<Long, List<Long>> distanceMatrix,
                       Map<Long, Map<Long, List<Long>>> truckDistanceMatrices,
                       int DEFAULT_HOUR_VALUE,
                       AlgorithmSpec algorithmSpec,
                       PreProcessPlan preProcessPlan,
                       Map<Long, List<Long>> distanceMatrixForJobScheduling) {
        super(trucksList, locationNodesList, source, initialPlan, distanceMatrix, truckDistanceMatrices, DEFAULT_HOUR_VALUE, algorithmSpec);
        this.distanceMatrixForJobScheduling = distanceMatrixForJobScheduling;
    }

    /**
     * In case of scheduling we want only the distances from source to other nodes (0-1, 0-2, etc, NOT 1-2
     */

//    private void createDistanceMatrixForJobScheduling() {
//        Long idOfSource = preProcessPlan.getSource().getId();
//
//        distanceMatrixForJobScheduling = preProcessPlan.getDistanceMatrix().entrySet()
//                .stream()
//                .filter(node -> node.getKey()
//                        .equals(idOfSource))
//                .collect(Collectors.toMap(Map.Entry::getKey,
//                        Map.Entry::getValue));
//    }

    //we also want truck distances



}
