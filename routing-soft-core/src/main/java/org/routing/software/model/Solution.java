package org.routing.software.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Solution {

    private Map<Integer, List<Integer>> truckLoadingTimeslot = new HashMap<>();
    private Map<Integer, Map<Integer, List<Integer>>> truckUnloadingTimeslotsPerLocationNode = new HashMap<>();

}
