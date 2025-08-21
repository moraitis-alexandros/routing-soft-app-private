//package org.routing.software;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.Persistence;
//import org.routing.software.generators.multischeduling.MultiSchedulingGenerator;
//import org.routing.software.generators.multischeduling.PlanToSolve;
//import org.routing.software.helper.Debug;
//import org.routing.software.jpos.AssignmentJpo;
//import org.routing.software.jpos.LocationNodeJpo;
//import org.routing.software.jpos.PlanJpo;
//import org.routing.software.jpos.TruckJpo;
//import org.routing.software.model.PreProcessPlan;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Main {
//
//    public static void main(String[] args) {
//
//        initializeUseCaseData();
//
//        //define the algorithm that should ran. use a simple handler
//
//        //the handler based on the algorithm (loads a preprocess plan)
//        // and then starts executing the algorithm
//
//
//
//
//
//    }
//
//    private static void initializeUseCaseData() {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("routing-PU");
//        EntityManager em = emf.createEntityManager();
//
//        List<TruckJpo> truckJpoList = new ArrayList<>();
//        List<LocationNodeJpo> locationNodeJpos = new ArrayList<>();
//        LocationNodeJpo source;
//
//        try {
//            em.getTransaction().begin();
//
//            //Initialize Truck1
//            TruckJpo truckJpoJpo1 = new TruckJpo();
//            truckJpoJpo1.setCapacity(5000L);
//            truckJpoJpo1.setDescription("Truck1");
//            truckJpoJpo1.setUnloadingTime(20L); //in minutes
//            truckJpoJpo1.setMaxSpeed(60L);
//            em.persist(truckJpoJpo1);
//            truckJpoList.add(truckJpoJpo1);
//
//            //Initialize Truck2
//            TruckJpo truckJpoJpo2 = new TruckJpo();
//            truckJpoJpo2.setCapacity(18000L);
//            truckJpoJpo2.setDescription("Truck2");
//            truckJpoJpo2.setUnloadingTime(40L); //in minutes
//            truckJpoJpo2.setMaxSpeed(50L);
//            em.persist(truckJpoJpo2);
//            truckJpoList.add(truckJpoJpo2);
//
//            //Initialize Source Location Node
//            LocationNodeJpo baseNode = new LocationNodeJpo();
//            baseNode.setSource(true);
//            baseNode.setCapacity(44000L);
//            baseNode.setDescription("BaseNode");
//            baseNode.setCoordinatesX(0L);
//            baseNode.setCoordinatesY(0L);
//            em.persist(baseNode);
//            locationNodeJpos.add(baseNode);
//            source = baseNode;
//
//            //Initialize DemandPoint (Tank) 1
//            LocationNodeJpo tank1 = new LocationNodeJpo();
//            tank1.setCapacity(22000L);
//            tank1.setDescription("Tank1");
//            tank1.setCoordinatesX(60L);
//            tank1.setCoordinatesY(60L);
//            em.persist(tank1);
//            locationNodeJpos.add(tank1);
//
//            //Initialize DemandPoint (Tank) 2
//            LocationNodeJpo tank2 = new LocationNodeJpo();
//            tank2.setCapacity(10000L);
//            tank2.setDescription("Tank2");
//            tank2.setCoordinatesX(120L);
//            tank2.setCoordinatesY(12L);
//            em.persist(tank2);
//            locationNodeJpos.add(tank2);
//
//            //Initialize DemandPoint (Tank) 3
//            LocationNodeJpo tank3 = new LocationNodeJpo();
//            tank3.setCapacity(12000L);
//            tank3.setDescription("Tank3");
//            tank3.setCoordinatesX(33L);
//            tank3.setCoordinatesY(40L);
//            em.persist(tank3);
//            locationNodeJpos.add(tank3);
//
//            //Initialize Assignments
//            //Create a preprocess plan populated only with the trucks and tanks we will use
//            //along with other data useful for routing plan creation
//            PlanJpo preProcessPlanJpo = new PlanJpo();
//            preProcessPlanJpo.setTimeslotLength(10L);
//
//            //Assign trucks preprocess
//            AssignmentJpo assignmentJpo1 = new AssignmentJpo();
//            assignmentJpo1.setTruck(truckJpoJpo1);
//            preProcessPlanJpo.addAssignment(assignmentJpo1);
//
//            AssignmentJpo assignmentJpo2 = new AssignmentJpo();
//            assignmentJpo2.setTruck(truckJpoJpo2);
//            preProcessPlanJpo.addAssignment(assignmentJpo2);
//
//            //Assign the tanks preprocess
//
//            AssignmentJpo assignmentJpoBaseNode = new AssignmentJpo();
//            assignmentJpoBaseNode.setLocationNode(baseNode);
//            preProcessPlanJpo.addAssignment(assignmentJpoBaseNode);
//
//            AssignmentJpo assignmentJpo3 = new AssignmentJpo();
//            assignmentJpo3.setLocationNode(tank1);
//            preProcessPlanJpo.addAssignment(assignmentJpo3);
//
//            AssignmentJpo assignmentJpo4 = new AssignmentJpo();
//            assignmentJpo4.setLocationNode(tank2);
//            preProcessPlanJpo.addAssignment(assignmentJpo4);
//
//            AssignmentJpo assignmentJpo5 = new AssignmentJpo();
//            assignmentJpo5.setLocationNode(tank3);
//            preProcessPlanJpo.addAssignment(assignmentJpo5);
//
//
//            em.persist(assignmentJpo1);
//            em.persist(assignmentJpo2);
//            em.persist(assignmentJpo3);
//            em.persist(assignmentJpo4);
//            em.persist(assignmentJpo5);
//            em.persist(preProcessPlanJpo);
//
//            em.getTransaction().commit();
//
//            PreProcessPlan preProcessPlan = new PreProcessPlan(truckJpoList, locationNodeJpos, source, preProcessPlanJpo);
//            preProcessPlan.setAlgorithmSpec(AlgorithmSpec.MultiScheduling);
//
//            PlanToSolve planToSolve = new PlanToSolve();
////            planToSolve.setPreProcessPlan(preProcessPlan);
//            //we need a separate class for decoupling
//
//            //Print the plans with the configured settings
////            Debug.print(preProcessPlan);
////            Debug.print(planToSolve);
//
//            AlgorithmHandler algorithmHandler = new AlgorithmHandler(preProcessPlan);
//
//            MultiSchedulingGenerator multiSchedulingGenerator = new MultiSchedulingGenerator();
//            multiSchedulingGenerator.createInitialChromosomePool();
//
////
////            <-----Test defineTimeslotThatWillActuallyUnload ----->//
//            System.out.println("----------TESTS----------");
////            Test the below method: //Expected start unloading timeslot: 1
//            List<Integer> truckUnloadingTimeslot = List.of(1,2,3);
//            List<Integer> locationNodeUnloadOccupiedNodes = List.of(5,6,7);
//            System.out.println("Expected Unloading Timeslot: 1 and Found:  " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot, locationNodeUnloadOccupiedNodes));
//
//            //Test the below method: //Expected start unloading timeslot: 8
//            List<Integer> truckUnloadingTimeslot2 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes2 = List.of(5,6,7);
//            System.out.println("Expected Unloading Timeslot: 8 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot2, locationNodeUnloadOccupiedNodes2));
//
//            //Test the below method: //Expected start unloading timeslot: 8
//            List<Integer> truckUnloadingTimeslot3 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes3 = List.of(5,6,7,11,12,13);
//            System.out.println("Expected Unloading Timeslot: 8 and Found " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot3, locationNodeUnloadOccupiedNodes3));
//
//            //Test the below method: //Expected start unloading timeslot: 14
//            List<Integer> truckUnloadingTimeslot4 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes4 = List.of(5,6,7,10,11,12,13);
//            System.out.println("Expected Unloading Timeslot: 14 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot4, locationNodeUnloadOccupiedNodes4));
//
//            //Test the below method: //Expected start unloading timeslot: 13
//            List<Integer> truckUnloadingTimeslot5 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes5 = List.of(5,6,7,10,11,12,16);
//            System.out.println("Expected Unloading Timeslot: 13 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot5, locationNodeUnloadOccupiedNodes5));
//
//            //Test the below method: //Expected start unloading timeslot: 17
//            List<Integer> truckUnloadingTimeslot6 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes6 = List.of(5,6,7,10,11,12,13,16);
//            System.out.println("Expected Unloading Timeslot: 17 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot6, locationNodeUnloadOccupiedNodes6));
//
//            //Test the below method: //Expected start unloading timeslot: 19
//            List<Integer> truckUnloadingTimeslot7 = List.of(8,9,10);
//            List<Integer> locationNodeUnloadOccupiedNodes7 = List.of(5,6,7,10,11,12,13,14,17,18,22,23);
//            System.out.println("Expected Unloading Timeslot: 19 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot7, locationNodeUnloadOccupiedNodes7));
//
//            //Test the below method: //Expected start unloading timeslot: 19
//            List<Integer> truckUnloadingTimeslot8 = List.of(14,15,16);
//            List<Integer> locationNodeUnloadOccupiedNodes8 = List.of(5,6,7,10,11,12,13,14,17,18,22,23);
//            System.out.println("Expected Unloading Timeslot: 19 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot8, locationNodeUnloadOccupiedNodes8));
//
//            //Test the below method: //Expected start unloading timeslot: 24
//            List<Integer> truckUnloadingTimeslot9 = List.of(20,21,22);
//            List<Integer> locationNodeUnloadOccupiedNodes9 = List.of(5,6,7,10,11,12,13,14,17,18,22,23);
//            System.out.println("Expected Unloading Timeslot: 24 and Found: " +
//                    multiSchedulingGenerator.defineTimeslotThatWillActuallyUnload(truckUnloadingTimeslot9, locationNodeUnloadOccupiedNodes9));
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot1 = 1;
//            List<Integer>  listForSpot1 = new ArrayList<>();
//            listForSpot1.add(1);
//            listForSpot1.add(2);
//            Map<Integer, List<Integer>> sourceTimeslotsThatAreOccupatedDueToLoading = new HashMap<>();
//            sourceTimeslotsThatAreOccupatedDueToLoading.put(spot1, listForSpot1);
//
//            List<Integer> truckPossibleLoadingTimeslots = new ArrayList<>();
//            truckPossibleLoadingTimeslots.add(2);
//
//            Map<Integer, Integer> resultMap1 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots,
//                    sourceTimeslotsThatAreOccupatedDueToLoading);
//            int result1 = resultMap1.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 3 and Found: " + result1);
//
//
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot2 = 1;
//            List<Integer>  listForSpot2 = new ArrayList<>();
//            listForSpot2.add(1);
//            listForSpot2.add(2);
//            listForSpot2.add(6);
//            Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading2 = new HashMap<>();
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading2.put(spot2, listForSpot2);
//
//            List<Integer> truckPossibleLoadingTimeslots2 = new ArrayList<>();
//            truckPossibleLoadingTimeslots2.add(3);
//            truckPossibleLoadingTimeslots2.add(4);
//
//
//            Map<Integer, Integer> resultMap2 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots2, locationNodesTimeslotsThatAreOccupatedDueToUnloading2);
//            int result2 = resultMap2.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 3 and Found: " + result2);
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot3 = 1;
//            List<Integer>  listForSpot3 = new ArrayList<>();
//            listForSpot3.add(1);
//            listForSpot3.add(2);
//            listForSpot3.add(6);
//            listForSpot3.add(7);
//            listForSpot3.add(8);
//            Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading3 = new HashMap<>();
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading3.put(spot3, listForSpot3);
//
//            List<Integer> truckPossibleLoadingTimeslots3 = new ArrayList<>();
//            truckPossibleLoadingTimeslots3.add(4);
//            truckPossibleLoadingTimeslots3.add(5);
//            truckPossibleLoadingTimeslots3.add(6);
//
//            Map<Integer, Integer> resultMap3 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots3, locationNodesTimeslotsThatAreOccupatedDueToUnloading3);
//            int result3 = resultMap3.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 9 and Found: " + result3);
//
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot4 = 1;
//            List<Integer>  listForSpot4 = new ArrayList<>();
//            listForSpot4.add(1);
//            listForSpot4.add(2);
//            listForSpot4.add(6);
//            listForSpot4.add(7);
//            listForSpot4.add(8);
//            listForSpot4.add(12);
//            listForSpot4.add(13);
//            Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading4 = new HashMap<>();
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading4.put(spot4, listForSpot4);
//
//            List<Integer> truckPossibleLoadingTimeslots4 = new ArrayList<>();
//            truckPossibleLoadingTimeslots4.add(4);
//            truckPossibleLoadingTimeslots4.add(5);
//            truckPossibleLoadingTimeslots4.add(6);
//            truckPossibleLoadingTimeslots4.add(8);
//
//
//            Map<Integer, Integer> resultMap4 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots4, locationNodesTimeslotsThatAreOccupatedDueToUnloading4);
//            int result4 = resultMap4.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 14 and Found: " + result4);
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot5 = 1;
//            List<Integer>  listForSpot5 = new ArrayList<>();
//            listForSpot5.add(1);
//            listForSpot5.add(2);
//            listForSpot5.add(6);
//            listForSpot5.add(7);
//            listForSpot5.add(8);
//            listForSpot5.add(12);
//            listForSpot5.add(13);
//            Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading5 = new HashMap<>();
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading5.put(spot5, listForSpot5);
//
//            List<Integer> truckPossibleLoadingTimeslots5 = new ArrayList<>();
//            truckPossibleLoadingTimeslots5.add(4);
//            truckPossibleLoadingTimeslots5.add(5);
//            truckPossibleLoadingTimeslots5.add(6);
//
//            Map<Integer, Integer> resultMap5 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots5, locationNodesTimeslotsThatAreOccupatedDueToUnloading5);
//            int result5 = resultMap5.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 9 and Found: " + result5);
//
//            //<-----Test findFreeTimeslotInSource ----->//
//            //Test with One Timeslot
//            Integer spot31 = 1;
//            Integer spot41 = 2;
//            List<Integer>  listForSpot31 = new ArrayList<>();
//            List<Integer>  listForSpot41 = new ArrayList<>();
//            listForSpot31.add(1);
//            listForSpot31.add(2);
//            listForSpot31.add(4);
//            listForSpot31.add(5);
//            listForSpot31.add(6);
//            listForSpot31.add(9);
//            listForSpot41.add(1);
//            listForSpot41.add(5);
//            listForSpot41.add(8);
//            Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading31 = new HashMap<>();
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading31.put(spot31, listForSpot31);
//            locationNodesTimeslotsThatAreOccupatedDueToUnloading31.put(spot41, listForSpot41);
//
//            List<Integer> truckPossibleLoadingTimeslots51 = new ArrayList<>();
//            truckPossibleLoadingTimeslots51.add(4);
//            truckPossibleLoadingTimeslots51.add(5);
//            truckPossibleLoadingTimeslots51.add(6);
//
//
//            Map<Integer, Integer> resultMap31 = multiSchedulingGenerator.findFreeTimeslotInSourceRevised(truckPossibleLoadingTimeslots51,
//                    locationNodesTimeslotsThatAreOccupatedDueToUnloading31);
//            int result31 = resultMap31.entrySet()
//                    .stream()
//                    .map(Map.Entry::getValue).toList().get(0);
//            System.out.println("Expected result: 9 and Found: " + result31);
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            em.getTransaction().rollback();
//        } finally {
//            em.close();
//            emf.close();
//        }
//    }
//
//
//}
