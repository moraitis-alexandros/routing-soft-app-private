package org.routing.software;

public enum OperationType {
        SPT,// (Shortest Processing Time): Select the operation with the shortest processing time.
        LPT,// (Longest Processing Time): Select the operation with the longest processing time.
        MWR, // (Most Work Remaining): Select the operation from the job with the most total remaining processing time.
        LWR, //(Least Work Remaining): Select the operation from the job with the least remaining processing time.
        MOR, //(Most Operations Remaining): Select the operation from the job with the most remaining operations.
        LOR //(Least Operations Remaining): Select the operation from the job with the fewest remaining operations.
//        FCFS, //(First Come First Served): Select the first operation in the machine's waiting queue.
//        R // (RandomUtil): Select an operation randomly.
}
