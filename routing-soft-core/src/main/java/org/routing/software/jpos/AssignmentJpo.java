package org.routing.software.jpos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "assignment")
public class AssignmentJpo implements IdentifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sequence")
    private Long sequence;

    @ManyToOne
    @JoinColumn(name = "location_node_id")
    private LocationNodeJpo locationNode;

    @ManyToOne
    @JoinColumn(name = "truck_id")
    private TruckJpo truck;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PlanJpo plan;

}
