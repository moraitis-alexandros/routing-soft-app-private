package org.routing.software.jpos;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "location")
public class LocationNodeJpo extends PlannableAsset implements IdentifiableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_source")
    private boolean isSource;

    @Column(name= "coordinates_x")
    private double coordinatesX;

    @Column(name = "coordinates_y")
    private double coordinatesY;

    @ManyToOne()
    private UserJpo userJpo; //default column name is userJpo

    @OneToMany(mappedBy = "locationNode")
    private List<AssignmentJpo> assignmentJpos;


}
