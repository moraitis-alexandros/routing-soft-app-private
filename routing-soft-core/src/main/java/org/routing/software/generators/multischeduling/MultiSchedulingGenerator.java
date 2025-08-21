package org.routing.software.generators.multischeduling;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.routing.software.OperationType;
import org.routing.software.helper.Debug;
import org.routing.software.model.*;
import org.routing.software.utils.RandomUtil;

import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//It will propably be the solver
public class MultiSchedulingGenerator {

    private PlanToSolve planToSolve;

    private Long initialPopulation = 10L;
    List<Assignment> assignmentPool = new ArrayList<>();
    List<List<OperationType>> chromosomePool = new ArrayList<>();
    List<Assignment> generatedSolution = new ArrayList<>();
    List<Assignment> remainingAssignmentPool = new ArrayList<>(assignmentPool); //two lists are independent but objects inside refer to the same
    List<Assignment> currentAssignmentPool = new ArrayList<>();
    Map<Integer, List<Integer>> sourceTimeslotsThatAreOccupatedDueToLoading = new HashMap<>(); //multiple unloading points for a single location node source
    Map<Integer, List<Integer>> locationNodesTimeslotsThatAreOccupatedDueToUnloading = new HashMap<>();
    private int NUMBER_OF_LOADING_SPOTS = 2;
    private Solution solution = new Solution();
    List<Assignment> tempSolutionList = new ArrayList<>(); //A list of temp solutions to compare the results

    //Phase 2 from the current assignment pool we start by creating
    //Based on the number of the assignments (we suppose that from phase 1 the demand is
    //fullfilled) we create the allele number

    public void createInitialChromosomePool() {

        int alleles = assignmentPool.size();
        alleles = 8; //TODO REMOVE

        //Produce initial ChromosomePopulation
        for (int i = 0; i < initialPopulation; i++) {
            //Produce the alleles of each chromosome
            List<OperationType> chromosome = new ArrayList<>();
            for (int j = 0; j < alleles; j++) {
                int randomNumber = RandomUtil.produceRandomNumber(0, OperationType.values().length);
                OperationType selectedOperationType = OperationType.values()[randomNumber];
                chromosome.add(selectedOperationType);
            }
            chromosomePool.add(chromosome);
        }
        Debug.print(chromosomePool);
        System.out.println();

        //also solution should be class that contains assignments
        //also check the abstract plan in order to be decoupled and can print
        //how much work per tank remains the least
        //how much work per truck
    }

    //we want a function that we will put the chromosome and  will create the solution
    private void calculateSolution(Chromosome chromosome) {


    }

    //function that picks current solution, then choses the operation that is defined by the allele,
    //then for each of the remaining assignments calculate the best assignment based on the operation type
    //when it finds the best assignment it puts it on a solution list, then it adds the assignment in
    //currentAssignmentPool and removes it remainingAssignmentPool
    public void findBestAssignment(Chromosome chromosome) {

        for (int i = 0; i < chromosome.getChromosome().size(); i++) {

            OperationType operationType = chromosome.getChromosome().get(i);
            Assignment assignment = alleleHandler(operationType);
            currentAssignmentPool.add(assignment);
            remainingAssignmentPool.remove(assignment);

        }
    }

    public Assignment alleleHandler(OperationType operationType) {


        if (operationType.equals(OperationType.LOR)) {
            //execute LOR
        } else if (operationType.equals(OperationType.MOR)) {
            //execute MOR
        } else if (operationType.equals(OperationType.LWR)) {
            //execute LWR
        } else if (operationType.equals(OperationType.MWR)) {
            //execute MWR
        } else if (operationType.equals(OperationType.LPT)) {
            LPTHandle();
        } else if (operationType.equals(OperationType.SPT)) {
            SPTHandle();
        }

        return new Assignment(); //TODO REMOVE
    }


    public void routeCreationBasedOnAssignmentList(List<Assignment> assignments) {
        long freeTimeslotInSource = 0;
        initializeLoadingSpotsOnSource();

        //for each assignment as ordered in list start placing
        for (int i = 0; i < remainingAssignmentPool.size(); i++) {

            Assignment assignment = assignments.get(0);
            Truck pickedTruck = assignment.getTruck();
            LocationNode locationNode = assignment.getLocationNodeList().get(0); //In case of Multi scheduling generator, only one assignment per Trck is allowed
            Map<Long, Map<Long, List<Long>>> truckDistanceMatrices =
                    planToSolve.getTruckDistanceMatrices();
            Map<Long, List<Long>> truckDistanceMatrix = truckDistanceMatrices.get(pickedTruck.getId());
            //We want only the matrix for source to tanks
            List<Long> distanceOfLocationNodesFromSource = truckDistanceMatrix.get(0L);
            Long travelTimeToNode = distanceOfLocationNodesFromSource.get(Math.toIntExact(locationNode.getId()));

            //Check in which timeslots it will unload on location node
            List<Integer> timeslotThatCouldUnloadOnLocationNode = new ArrayList<>();
            long startUnloadingTimeslot = freeTimeslotInSource + travelTimeToNode + 1;
            long finishUnloadingTimeslot = startUnloadingTimeslot + pickedTruck.getConvertedUnloadingTime();

            for (int j = (int) startUnloadingTimeslot; j < (int) finishUnloadingTimeslot; j++) {
                timeslotThatCouldUnloadOnLocationNode.add(j);
            }

            List<Integer> occupiedTimeslotsForNode = locationNodesTimeslotsThatAreOccupatedDueToUnloading.get(locationNode.getId());
            long timeslotThatWillActuallyLoad = defineTimeslotThatWillActuallyUnload(timeslotThatCouldUnloadOnLocationNode, occupiedTimeslotsForNode);

            long timeslotThatItWillBeBackToSource = timeslotThatWillActuallyLoad + pickedTruck.getConvertedUnloadingTime() + travelTimeToNode;

            int timeslotThatCanLoadOnSource = (int)(timeslotThatItWillBeBackToSource + pickedTruck.getConvertedUnloadingTime());
            List<Integer> timeslotOccupiedThatCanLoadOnSource = findTruckOperationOccupiedTimeslots(pickedTruck, timeslotThatCanLoadOnSource);

            Map<Integer, Integer> freeTimeslotAndSpotInSource
                    = findFreeTimeslotInSourceRevised(timeslotOccupiedThatCanLoadOnSource, this.sourceTimeslotsThatAreOccupatedDueToLoading);

            //Populate the relative fields on the Assignment based on the above results
            //get the associated spot
            assignment.setSourceLoadingSpot(freeTimeslotAndSpotInSource.entrySet()
                            .stream()
                            .map(Map.Entry::getKey).toList().get(0));
            //get the associated timeslot

            assignment.setSourceLoadingTimeslot(freeTimeslotAndSpotInSource.entrySet()
                    .stream()
                    .map(Map.Entry::getValue).toList().get(0));

            //The info we need have stored on Assignment Object
            //Now we want to store this assignment object in a temp solution in order to compare with other
            //solution and keep the one that fits best based on a certain criterion.
            tempSolutionList.add(assignment);
        }

    }

    /**
     * It will take the timeslots that the investigated truck could actually load on its n-th trip
     * and based on the availability of the locationNode assigned it will check overlapping and will return
     * the timeslot that actually will unload
     *
     * @param timeslotThatCouldUnloadOnLocationNode
     * @return the timeslot that will actually unload
     */
    public long defineTimeslotThatWillActuallyUnload(List<Integer> timeslotThatCouldUnloadOnLocationNode,
                                                     List<Integer> occupiedTimeslotsForNode) {

        //Get the first and the last unload timeslots of locationNode
        int lastUnloadTimeslotForLocationNode = occupiedTimeslotsForNode.get(occupiedTimeslotsForNode.size() - 1);
        int firstUnloadTimeslotForLocationNode = occupiedTimeslotsForNode.get(0);

        //Get the first and last unload timeslots of truck
        int firstUnloadTimeslotForTruck = timeslotThatCouldUnloadOnLocationNode.get(0);

        //Check the case, locationNode does not have any occupied timeslots
        if (occupiedTimeslotsForNode.isEmpty()) {
            return firstUnloadTimeslotForTruck;
        }

        //Check the case trucks starts loading after last unload timeslot of occupationList
        if (firstUnloadTimeslotForTruck > lastUnloadTimeslotForLocationNode) {
            return firstUnloadTimeslotForTruck;
        }

        //Check the case trucks starts loading just in the last unload timeslot of occupationList
        if (firstUnloadTimeslotForTruck == lastUnloadTimeslotForLocationNode) {
            return firstUnloadTimeslotForTruck + 1;
        }

        //Check the case it starts loading before first unload timeslot and CAN Unload
        if ((truckArrivesBeforeFirstUnloadTimeslotForLocationNode(firstUnloadTimeslotForTruck, firstUnloadTimeslotForLocationNode))
                && (truckUnloadingIsAllowed(firstUnloadTimeslotForTruck, timeslotThatCouldUnloadOnLocationNode.size(), firstUnloadTimeslotForLocationNode))) {
            return firstUnloadTimeslotForTruck;
        }

        //If the loop arrives at this point it means that the unloading will be probably
        //between int firstOccupiedTimeslotForLocationNode.get(0) and firstOccupiedTimeslotForLocationNode.get(firstOccupiedTimeslotForLocationNode.size() - 1)
        //and if all the previous space is occupied it will be placed firstOccupiedTimeslotForLocationNode.get(firstOccupiedTimeslotForLocationNode.size() - 1) + 1
        for (int i = firstUnloadTimeslotForLocationNode + 1; i <= lastUnloadTimeslotForLocationNode; i++) {

            //for each timeslot check if the timeslot exists in occupiedTimeslotsForNode
            //if it exists then we do not need to check and we continue to the next timeslot
            if (!truckArrivesBeforeFirstUnloadTimeslotForLocationNode(firstUnloadTimeslotForTruck, i)) {
                continue;
            }

            if (occupiedTimeslotsForNode.contains(i)) {
                continue;
            }

            //now we check if from the timeslot we currently investigate (i) can fit a solution
            //to fit a solution the next j timeslots in iteration with size equal to the unloading time
            //must not be in occupiedTimeslotsForNode
            boolean isOccupied = false;
            for (int j = i; j < i + timeslotThatCouldUnloadOnLocationNode.size(); j++) {
                if (occupiedTimeslotsForNode.contains(j)) {
                    isOccupied = true;
                    break;
                }
            }

            //if the timeslot we examine is occupied by locationNode then increase
            if (isOccupied) {
                continue;
            } else {
                return i;
            }
        }
        return lastUnloadTimeslotForLocationNode + 1;
    }

    private boolean truckArrivesBeforeFirstUnloadTimeslotForLocationNode(int truckFirstUnloadTimeslot,
                                                                         int firstOccupiedTimeslotForLocationNode) {
        return truckFirstUnloadTimeslot <= firstOccupiedTimeslotForLocationNode;
    }

    private boolean truckUnloadingIsAllowed(int truckUnloadTimeslot,
                                            int truckTotalUnloadingTimeInTimeslots,
                                            int occupiedTimeslotForLocationNode) {
        return truckUnloadTimeslot + truckTotalUnloadingTimeInTimeslots - 1 < occupiedTimeslotForLocationNode;
    }


    /**
     * We will use the same function as the unloading process for each spot
     * @return
     */
    public Map<Integer, Integer> findFreeTimeslotInSourceRevised(List<Integer> timeslotsThatCanLoadOnSource, Map<Integer, List<Integer>> sourceTimeslotsThatAreOccupatedDueToLoading) {
        int minTimeslot = Integer.MAX_VALUE;
        int selectedSpot = 0;
        NUMBER_OF_LOADING_SPOTS = sourceTimeslotsThatAreOccupatedDueToLoading.size(); //todo remove
        Map<Integer, Integer> freeTimeslotAndSpotInSource = new HashMap<>();

        for (int spot = 0; spot < NUMBER_OF_LOADING_SPOTS; spot++) {

            List<Integer> occupiedLoadingTimeslotsForSpot = sourceTimeslotsThatAreOccupatedDueToLoading.get(spot + 1);
            long timeslotThatStartLoadingOnSpot =
                    defineTimeslotThatWillActuallyUnload(timeslotsThatCanLoadOnSource, occupiedLoadingTimeslotsForSpot);

            if (minTimeslot > timeslotThatStartLoadingOnSpot) {
                minTimeslot = (int)timeslotThatStartLoadingOnSpot;
                selectedSpot = spot + 1;
                freeTimeslotAndSpotInSource = new HashMap<>();
                freeTimeslotAndSpotInSource.put(selectedSpot, minTimeslot);
            }
            //populate the sourceTimeslotsThatAreOccupationDueToLoading on the certain spot
            for (int i = 0; i < timeslotsThatCanLoadOnSource.size(); i++) {
                sourceTimeslotsThatAreOccupatedDueToLoading.get(selectedSpot).add(i + 1);
            }
        }
        return freeTimeslotAndSpotInSource;
        }


    /**
     *  Initialize the arraylists for each unloading spot
     */
    private void initializeLoadingSpotsOnSource() {
        for (int i = 0; i < NUMBER_OF_LOADING_SPOTS; i++) {
            int spotId = i + 1;
            List<Integer> spotList = new ArrayList<>();
            sourceTimeslotsThatAreOccupatedDueToLoading.put(spotId, spotList);
        }
    }

    /**
     * Provide the truck and the starting timeslot and return a list of the occupied timeslots
     */
    public List<Integer> findTruckOperationOccupiedTimeslots(Truck truck, int startingOperationTimeslot) {
        List<Integer> timeslotOccupiedThatCanLoadOnSource = new ArrayList<>();
        for (int o = 0; o < truck.getConvertedUnloadingTime(); o++) {
            timeslotOccupiedThatCanLoadOnSource.add(startingOperationTimeslot + o + 1);
        }
        return timeslotOccupiedThatCanLoadOnSource;
    }

    /**
     * This function takes a candidate list of assignments (tempSolutionList) - (with populated relative fields ,ie each of one has been populated
     * using routeCreationBasedOnAssignmentList method) and selects the best based on Shortest Processing Time.
     * IMPORTANT -> we suppose that the processing time, is defined as the time that the truck starts loading till the time that the
     * the truck is available for loading again in source.
     */
    private void SPTHandle() {
        int availableTimeslot = findTimeslotWhichIsFreeBasedOnCurrentAssignmentPool();

        int min = Integer.MAX_VALUE;
        Assignment pickedAssignment = new Assignment();

        for (Assignment assignment : tempSolutionList) {
                if (assignment.getSourceLoadingTimeslot() < min) {
                    pickedAssignment = assignment;
                    min = assignment.getSourceLoadingTimeslot();
                }
        }
        remainingAssignmentPool.remove(pickedAssignment);
        currentAssignmentPool.add(pickedAssignment);
    }

    /**
     * This function takes a candidate list of assignments (tempSolutionList) - (with populated relative fields ,ie each of one has been populated
     * using routeCreationBasedOnAssignmentList method) and selects the best based on Longest Processing Time.
     * IMPORTANT -> we suppose that the processing time, is defined as the time that the truck starts loading till the time that the
     * the truck is available for loading again in source.
     */
    private void LPTHandle() {
        int availableTimeslot = findTimeslotWhichIsFreeBasedOnCurrentAssignmentPool();

        int max = 0;
        Assignment pickedAssignment = new Assignment();

        for (Assignment assignment : tempSolutionList) {
            if (assignment.getSourceLoadingTimeslot() > max) {
                pickedAssignment = assignment;
                max = assignment.getSourceLoadingTimeslot();
            }
        }
        remainingAssignmentPool.remove(pickedAssignment);
        currentAssignmentPool.add(pickedAssignment);
    }

    private int findTimeslotWhichIsFreeBasedOnCurrentAssignmentPool() {
        //find the last assignment
        int lastIndex = (remainingAssignmentPool.size() - 1);
        Assignment assignment = remainingAssignmentPool.get(lastIndex);
        return assignment.getSourceLoadingTimeslot() + assignment.getTruck().getConvertedUnloadingTime().intValue();

    }

}





