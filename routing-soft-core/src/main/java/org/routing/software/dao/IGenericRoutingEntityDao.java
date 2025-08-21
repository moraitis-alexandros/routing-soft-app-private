package org.routing.software.dao;

import java.util.List;
import java.util.Optional;

public interface IGenericRoutingEntityDao<T> {

    Optional<T> insert(T t);
    List<T> getAllEntitiesByUserUUID(Object userUUID);
    Optional<T> getSingleEntityByIdByUserUUID(Object entityId, Object userUUID);
    List<T> getAllEntitiesByPlanIdAndByUserUUID(Object planId, Object userUUID);
    boolean isEntityExists(Object entityId, Object userUUID);
    boolean hasPlanAssigned(Object entityId, Object userUUID);
    Optional<T> deleteEntity(Object entityId, Object userUUID);

}

