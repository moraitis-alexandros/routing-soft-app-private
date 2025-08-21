package org.routing.software.service;

import org.routing.software.dtos.TruckInsertDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ITruckService {

    Optional<TruckReadOnlyDto> insertTruck(TruckInsertDto truckInsertDto, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException;
    List<TruckReadOnlyDto> getAllTrucksByUserUUID(String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException;
    Optional<TruckReadOnlyDto> getSingleTruckByIdByUserUUID(Long truckId, String userUUID) throws EntityInvalidArgumentException,EntityNotFoundException;
    List<TruckReadOnlyDto> getAllTrucksByPlanIdAndByUserUUID(Long planId, String userUUID) throws EntityInvalidArgumentException,EntityNotFoundException;
    boolean isTruckExists(Long truckId, String userUUID);
    boolean hasPlanAssigned(Long truckId, String userUUID);
    Optional<TruckReadOnlyDto> deleteTruck(Long truckId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException;

}
