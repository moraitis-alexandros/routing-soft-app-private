package org.routing.software.jpos;


import jakarta.persistence.*;
import lombok.*;
import org.routing.software.core.RoleType;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "users")
public class UserJpo extends AbstractEntity implements IdentifiableEntity, Principal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Column
    private String password;

    @Column
    private String confirmationToken;

    @Column(name = "role")
    @Enumerated(EnumType.STRING) // του λεω πως θα αποθηκευτει μεσα στη βαση αλλιως θα αποθηκευτει με το ordinal (1,2 κοκ)
    private RoleType roleType;

    @OneToMany(mappedBy = "userJpo", cascade = CascadeType.PERSIST)
    List<TruckJpo> trucks;

    @OneToMany(mappedBy = "userJpo", cascade = CascadeType.PERSIST)
    //mapped by -> “Hey, look in the TruckJpo class for the field called userJpo. That’s the owner of the relationship.”
    List<LocationNodeJpo> locationNodes;

    @OneToMany(mappedBy = "userJpo", cascade = CascadeType.PERSIST)
    List<PlanJpo> plans;

    @Override
    public String getName() {
        return getUuid(); // εδώ επιστρέφω αυτό που θέλω να αναγνωρίζει τον principal, πχ username
    }

    public void addObject(Object object) {
        if (object instanceof PlanJpo) {
            plans.add((PlanJpo) object);
        } else if (object instanceof TruckJpo) {
            trucks.add((TruckJpo) object);
        } else if (object instanceof LocationNodeJpo) {
            locationNodes.add((LocationNodeJpo) object);
        } else {
            //TODO LOGGER the exception
            throw new IllegalArgumentException("Unsupported type: " + object.getClass());
        }
    }

}