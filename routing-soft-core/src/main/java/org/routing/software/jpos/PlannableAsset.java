package org.routing.software.jpos;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class PlannableAsset extends AbstractEntity {

    @Column(name = "capacity")
    private Long capacity;

    @Column(name = "description")
    private String description;

}
