package org.routing.software.service;

import org.routing.software.dtos.*;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IPlanService {
    Optional<PlanReadOnlyDto> insertPlan(PlanInsertDto planInsertDto, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException;
    List<PlanReadOnlyDto> getAllPlansByUserUUID(String userUUID) throws EntityNotFoundException;
    Optional<PlanReadOnlyDto> getSinglePlanByIdByUserUUID(Long planId, String userUUID) throws EntityNotFoundException, EntityInvalidArgumentException;
    boolean isPlanExists(Long planId, String userUUID);
    Optional<PlanReadOnlyDto> deletePlan(Long planId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException;
    Optional<PlanReadOnlyDto> solvePlan(Long planId, String uuid) throws EntityNotFoundException, EntityInvalidArgumentException;
}
