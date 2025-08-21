package org.routing.software.helper;

import org.routing.software.OperationType;
import org.routing.software.jpos.LocationNodeJpo;
import org.routing.software.model.PreProcessPlan;

import java.util.List;
import java.util.Map;

/**
 * A helper method util for printing stuff, used mostly for debugging
 */
public class Debug {

    private Debug() {

    }

//    //for debug
//    public static void print(PreProcessPlan plan) {
//
//        printMatrixAsTable("Distance Matrix for" + plan.getAlgorithmSpec().name(),
//                plan.getDistanceMatrix(), plan.getLocationNodesList());
//
//        // For each truck
//        plan.getTruckDistanceMatrices().forEach((truckId, matrix) -> {
//            System.out.println("\n=== Travel Time Matrix for Truck ID: " + truckId + " ===");
//            printMatrixAsTable("Truck " + truckId + " Matrix", matrix, plan.getLocationNodesList());
//        });
//    }

    private static void printMatrixAsTable(String title, Map<Long, List<Long>> matrix, List<LocationNodeJpo> nodes) {
        System.out.println("\n" + title);

        // Header row
        System.out.print(String.format("%10s", "From \\ To"));
        for (LocationNodeJpo target : nodes) {
            System.out.print(String.format("%10s", target.getId()));
        }
        System.out.println();

        // Matrix body
        for (LocationNodeJpo source : nodes) {
            Long sourceId = source.getId();
            System.out.print(String.format("%10s", sourceId));
            List<Long> row = matrix.get(sourceId);
            if (row != null) {
                for (Long value : row) {
                    System.out.print(String.format("%10s", value));
                }
            } else {
                for (int i = 0; i < nodes.size(); i++) {
                    System.out.print(String.format("%10s", "-"));
                }
            }
            System.out.println();
        }
    }

    /**
     * Prints the contents of the chromosome pool.
     * Each chromosome is printed on its own line, with operation types listed in order.
     *
     * @param chromosomePool List of chromosomes, each a list of OperationType
     */
    public static void print(List<List<OperationType>> chromosomePool) {
        System.out.println("\n=== Chromosome Pool Debug ===");

        int index = 0;
        for (List<OperationType> chromosome : chromosomePool) {
            System.out.printf("Chromosome %02d: ", index++);
            for (OperationType op : chromosome) {
                System.out.print(op.name() + " ");
            }
            System.out.println();
        }
    }
}




