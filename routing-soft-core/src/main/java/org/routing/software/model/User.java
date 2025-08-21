package org.routing.software.model;


import lombok.*;
import org.routing.software.core.RoleType;
import org.routing.software.jpos.AbstractEntity;
import org.routing.software.jpos.IdentifiableEntity;

import java.security.Principal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User implements Principal {

    private String uuid;

    private String username;

    private String password;

    private RoleType roleType;

    @Override
    public String getName() {
            return uuid; // principal used by Principal interface in CustomSecurityContext
    }
}
