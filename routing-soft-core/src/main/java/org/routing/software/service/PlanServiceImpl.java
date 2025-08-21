package org.routing.software.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.routing.software.AlgorithmSpec;
import org.routing.software.PlanStatus;
import org.routing.software.dao.*;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.dtos.PlanInsertDto;
import org.routing.software.dtos.PlanReadOnlyDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.jpos.AssignmentJpo;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.jpos.TruckJpo;
import org.routing.software.jpos.UserJpo;
import org.routing.software.mappers.LocationNodeMapper;
import org.routing.software.mappers.PlanMapper;
import org.routing.software.mappers.TruckMapper;
import org.routing.software.model.Truck;
import org.routing.software.model.TspAlgorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class PlanServiceImpl implements IPlanService{


    private final IUserDao userDao;
    private final IPlanDao planDao;
    private final LocationNodeMapper locationNodeMapper;
    private final TruckMapper truckMapper;
    private final PlanMapper planMapper;
    private final TspAlgorithm tspAlgorithm;

    @Override
    public Optional<PlanReadOnlyDto> insertPlan(PlanInsertDto planInsertDto, String userUUID)  throws EntityInvalidArgumentException, EntityNotFoundException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            //First persist an unsubmitted plan in order because plan id is needed for persistence of assignment
            PlanJpo planJpo = new PlanJpo();
            planJpo.setTimeslotLength(planInsertDto.getTimeslotLength());
            planJpo.setAlgorithmSpec(AlgorithmSpec.valueOf(planInsertDto.getAlgorithmSpec()));
            planJpo.setPlanStatus(PlanStatus.UNSUBMITTED);
            planJpo.setUserJpo(userJpoOptional.get());
            userJpoOptional.get().addObject(planJpo);
            Optional<PlanJpo> planJpoOptional = planDao.insert(planJpo);


            if (planJpoOptional.isEmpty()) {
                throw new EntityInvalidArgumentException("InvalidArgument", "No plan inserted.");
            }

            //No need to convert locationNodeDto and TruckDto because they are already
            //persisted. When the plan is inserted they are already exists in DB

            List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList = planInsertDto.getLocationNodeList();

            locationNodeReadOnlyDtoList.forEach(locationNodeReadOnlyDto -> {
                AssignmentJpo assignmentJpo = new AssignmentJpo();
                assignmentJpo.setPlan(planJpoOptional.get());
                assignmentJpo.setLocationNode(locationNodeMapper.locationNodeReadOnlyDtoToLocationNodeJpo(locationNodeReadOnlyDto));
                planJpo.addAssignment(assignmentJpo);
            });

            List<TruckReadOnlyDto> truckReadOnlyDtoList = planInsertDto.getTrucksList();
            truckReadOnlyDtoList.forEach(truckReadOnlyDto -> {
                AssignmentJpo assignmentJpo = new AssignmentJpo();
                assignmentJpo.setPlan(planJpoOptional.get());
                assignmentJpo.setTruck(truckMapper.truckReadOnlyDtoToTruckJpo(truckReadOnlyDto));
                planJpo.addAssignment(assignmentJpo);
            });

            planJpo.getAssignments().size(); // force initialization before commit to fetch all relative entities
            JpaHelper.commitTransaction();

            PlanReadOnlyDto planReadOnlyDto = planMapper.planJpoToPlanReadOnlyDto(planJpo);
            List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoListAfter = new ArrayList<>();
            List<TruckReadOnlyDto> truckReadOnlyDtoListAfter = new ArrayList<>();

            planJpo.getAssignments().forEach(assignmentJpo -> {
                if (assignmentJpo.getTruck() == null) {
                    //if truck is null then it is location node assignment
                    LocationNodeReadOnlyDto locationNodeReadOnlyDto = locationNodeMapper
                            .locationNodeToLocationNodeReadOnlyDto(locationNodeMapper
                                    .locationNodeJpoToLocationNode(assignmentJpo.getLocationNode()));
                    locationNodeReadOnlyDtoListAfter.add(locationNodeReadOnlyDto);
                }

                if (assignmentJpo.getLocationNode() == null) {
                    //if truck is null then it is location node assignment
                    TruckReadOnlyDto truckReadOnlyDto = truckMapper
                            .truckToTruckReadOnlyDto(truckMapper
                                    .truckJpoToTruck(assignmentJpo.getTruck()));
                    truckReadOnlyDtoListAfter.add(truckReadOnlyDto);
                }

                planReadOnlyDto.setLocationNodeReadOnlyDtoList(locationNodeReadOnlyDtoListAfter);
                planReadOnlyDto.setTruckReadOnlyDtoList(truckReadOnlyDtoListAfter);

            });

            return Optional.of(planReadOnlyDto);

        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public List<PlanReadOnlyDto> getAllPlansByUserUUID(String userUUID) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            List<PlanJpo> planJpoList = planDao.getAllEntitiesByUserUUID(userUUID);
            JpaHelper.commitTransaction();

            if (planJpoList.isEmpty()) {
                return new ArrayList<>();
            }

            //if truck is null then it is location node assignment

            return planJpoList.stream().map(planJpo -> {

                PlanReadOnlyDto planReadOnlyDto = planMapper.planJpoToPlanReadOnlyDto(planJpo);
                List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoListAfter = new ArrayList<>();
                List<TruckReadOnlyDto> truckReadOnlyDtoListAfter = new ArrayList<>();

                planJpo.getAssignments().forEach(assignmentJpo -> {
                    if (assignmentJpo.getTruck() == null) {
                        //if truck is null then it is location node assignment
                        LocationNodeReadOnlyDto locationNodeReadOnlyDto = locationNodeMapper
                                .locationNodeToLocationNodeReadOnlyDto(locationNodeMapper
                                        .locationNodeJpoToLocationNode(assignmentJpo.getLocationNode()));
                        locationNodeReadOnlyDtoListAfter.add(locationNodeReadOnlyDto);
                    }

                    if (assignmentJpo.getLocationNode() == null) {
                        //if truck is null then it is location node assignment
                        TruckReadOnlyDto truckReadOnlyDto = truckMapper
                                .truckToTruckReadOnlyDto(truckMapper
                                        .truckJpoToTruck(assignmentJpo.getTruck()));
                        truckReadOnlyDtoListAfter.add(truckReadOnlyDto);
                    }

                    planReadOnlyDto.setLocationNodeReadOnlyDtoList(locationNodeReadOnlyDtoListAfter);
                    planReadOnlyDto.setTruckReadOnlyDtoList(truckReadOnlyDtoListAfter);

                });
                return planReadOnlyDto;
            }).toList();

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public Optional<PlanReadOnlyDto> getSinglePlanByIdByUserUUID(Long planId, String userUUID) throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            List<PlanReadOnlyDto> planReadOnlyDtoList = getAllPlansByUserUUID(userUUID)
                    .stream()
                    .filter(planReadOnlyDto -> planReadOnlyDto.getId()
                            .equals(planId))
                    .toList();

            if (planReadOnlyDtoList.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No plan found with that uuid");
            }

            if (planReadOnlyDtoList.size() > 1) {
                throw new EntityInvalidArgumentException("InvalidArgument", "There should not exist more than one plans with the same uuid");
            }

            return Optional.of(planReadOnlyDtoList.get(0));
        } catch (EntityNotFoundException | EntityInvalidArgumentException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
    }
}

    @Override
    public boolean isPlanExists(Long planId, String userUUID) {
        return planDao.isEntityExists(planId, userUUID);
    }

    @Override
    public Optional<PlanReadOnlyDto> deletePlan(Long planId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(uuid);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            Optional<PlanJpo> planJpoOptional = planDao.getSingleEntityByIdByUserUUID(planId, uuid);


            if (planJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No plan found with that id");
            }
            PlanReadOnlyDto planReadOnlySummaryDto = planMapper.planJpoToPlanReadOnlyDto(planJpoOptional.get());

            Optional<PlanJpo> deletedPlanJpoOptional = planDao.deleteEntity(planId, uuid);
            JpaHelper.commitTransaction();
            if (deletedPlanJpoOptional.isEmpty()) {
                throw new EntityInvalidArgumentException("InvalidArgument", "There was an error in the persist process.");
            }

            return Optional.of(planReadOnlySummaryDto);

        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public  Optional<PlanReadOnlyDto> solvePlan(Long planId, String uuid) throws EntityNotFoundException, EntityInvalidArgumentException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(uuid);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            Optional<PlanJpo> planJpoOptional = planDao.getSingleEntityByIdByUserUUID(planId, uuid);

            if (planJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No plan found with that id");
            }

            JpaHelper.commitTransaction();
            Optional<PlanReadOnlyDto> planReadOnlyDtoOptional = getSinglePlanByIdByUserUUID(planId,uuid);

            PlanReadOnlyDto planToSolve = tspAlgorithm.solve(planReadOnlyDtoOptional.get());



            return Optional.of(planToSolve);
        } catch (EntityNotFoundException| EntityInvalidArgumentException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }
}
