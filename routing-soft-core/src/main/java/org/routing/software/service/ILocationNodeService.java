package org.routing.software.service;

import org.routing.software.dtos.LocationNodeInsertDto;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ILocationNodeService {

    Optional<LocationNodeReadOnlyDto> insertNode(LocationNodeInsertDto locationNodeInsertDto, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException;

    List<LocationNodeReadOnlyDto> getAllNodesByUserUUID(String userUUID) throws EntityNotFoundException;

    Optional<LocationNodeReadOnlyDto> getSingleNodeByIdByUserUUID(Long nodeId, String userUUID) throws EntityNotFoundException;

    List<LocationNodeReadOnlyDto> getAllNodesByPlanIdAndByUserUUID(Long planId, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException;

    boolean isNodeExists(Long nodeId, String userUUID);

    boolean hasPlanAssigned(Long nodeId, String userUUID);

    Optional<LocationNodeReadOnlyDto> deleteNode(Long nodeId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException;

}
