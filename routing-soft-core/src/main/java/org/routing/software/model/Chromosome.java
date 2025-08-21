package org.routing.software.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.routing.software.OperationType;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Chromosome {

    private List<OperationType> chromosome;

    public void addAllele(OperationType operationType) {

        if (chromosome == null) {
            chromosome = new ArrayList<>();
        }
        chromosome.add(operationType);
    }

    public void addAllele(OperationType operationType, int index) {
        if (chromosome == null) {
            chromosome = new ArrayList<>();
        }
        chromosome.add(index, operationType);
    }
}
