package org.routing.software.mappers;

import org.mapstruct.Mapper;
import org.routing.software.core.RoleType;

/**
 * A utility class for User Mapper interface in order to transform
 * role enum to string and vice verca
 */
@Mapper(componentModel = "cdi")
public interface RoleMapper {

        String mapRoleTypeToString(RoleType roleType);

        RoleType mapStringToRoleType(String role);
}
