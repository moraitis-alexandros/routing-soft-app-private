package org.routing.software;

import org.routing.software.model.PreProcessPlan;

public class AlgorithmHandler {

    public AlgorithmHandler(PreProcessPlan preProcessPlan) {

        if (preProcessPlan.getAlgorithmSpec().equals(AlgorithmSpec.MultiScheduling)) {
            //We call MultiSchedulingGenerator
            //We pass the preprocess plan
            //The generator should initialize stuff and then it will call the solver
            //The solver will solve and persist the results and then print the results
        }


    }
}
