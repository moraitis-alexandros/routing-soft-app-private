package org.routing.software.service;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.routing.software.dao.IUserDao;
import org.routing.software.dtos.TruckInsertDto;
import org.routing.software.dao.ITruckDao;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.jpos.TruckJpo;
import org.routing.software.jpos.UserJpo;
import org.routing.software.mappers.TruckMapper;
import org.routing.software.model.Truck;
import java.util.List;
import java.util.Optional;


@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class TruckServiceImpl implements ITruckService{

    private final ITruckDao truckDao;
    private final IUserDao userDao;
    private final TruckMapper truckMapper;

    @Override
    public Optional<TruckReadOnlyDto> insertTruck(TruckInsertDto truckInsertDto, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            UserJpo userJpo = userJpoOptional.get();
            Truck truck = truckMapper.truckInsertDtoToTruck(truckInsertDto);
            TruckJpo truckJpo = truckMapper.truckToTruckJpo(truck);
            userJpo.addObject(truckJpo);
            truckJpo.setUserJpo(userJpo);
            Optional<TruckJpo> persistedTruckJpo = truckDao.insert(truckJpo);
            userDao.update(userJpo); //user just needs an update, not new persist
            JpaHelper.commitTransaction();

            TruckReadOnlyDto truckReadOnlyDto;
            if (persistedTruckJpo.isPresent()) {
                Truck persistedTruck = truckMapper.truckJpoToTruck(persistedTruckJpo.get());
                truckReadOnlyDto = truckMapper.truckToTruckReadOnlyDto(persistedTruck);
                return Optional.of(truckReadOnlyDto);
            } else {
                throw new EntityInvalidArgumentException("InvalidArgument", "There was an error in the persist process.");
            }

        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public List<TruckReadOnlyDto> getAllTrucksByUserUUID(String userUUID) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }
            List<TruckJpo> truckJpoList = truckDao.getAllEntitiesByUserUUID(userUUID);
            JpaHelper.commitTransaction();

            List<TruckReadOnlyDto> truckReadOnlyDtoList = truckJpoList
                    .stream()
                    .map(truckJpo -> truckMapper.truckJpoToTruck(truckJpo))
                    .map(truck -> truckMapper.truckToTruckReadOnlyDto(truck))
                    .toList();

            return truckReadOnlyDtoList;

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public Optional<TruckReadOnlyDto> getSingleTruckByIdByUserUUID(Long truckId, String userUUID) throws EntityNotFoundException {

            try {
                JpaHelper.beginTransaction();
                Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

                if (userJpoOptional.isEmpty()) {
                    throw new EntityNotFoundException("NotFound", "No user found with that uuid");
                }

                Optional<TruckJpo> truckJpoOptional = truckDao.getSingleEntityByIdByUserUUID(truckId, userUUID);
                JpaHelper.commitTransaction();

                if (truckJpoOptional.isEmpty()) {
                    throw new EntityNotFoundException("NotFound", "No truck found with that id");
                }

                Truck truck = truckMapper.truckJpoToTruck(truckJpoOptional.get());
                TruckReadOnlyDto truckReadOnlyDto = truckMapper.truckToTruckReadOnlyDto(truck);

                return Optional.of(truckReadOnlyDto);

            } catch (EntityNotFoundException e) {
                JpaHelper.rollbackTransaction();
                throw e;
            } finally {
                JpaHelper.closeEntityManager();
            }
        }

    @Override
    public List<TruckReadOnlyDto> getAllTrucksByPlanIdAndByUserUUID(Long planId, String userUUID) throws EntityNotFoundException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            List<TruckJpo> truckJpoList = truckDao.getAllEntitiesByPlanIdAndByUserUUID(planId, userUUID);

            if (truckJpoList.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No truck found with that id");
            }

            List<TruckReadOnlyDto> truckReadOnlyDtoList = truckJpoList
                    .stream()
                    .map(truckJpo -> truckMapper.truckJpoToTruck(truckJpo))
                    .map(truck -> truckMapper.truckToTruckReadOnlyDto(truck))
                    .toList();
            return truckReadOnlyDtoList;
        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean isTruckExists(Long truckId, String userUUID) {

        try {
            JpaHelper.beginTransaction();
            boolean isTruckExists = truckDao.isEntityExists(truckId, userUUID);
            JpaHelper.commitTransaction();
            return isTruckExists;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean hasPlanAssigned(Long truckId, String userUUID) {
        try {
            JpaHelper.beginTransaction();
            boolean hasPlanAssigned =  truckDao.hasPlanAssigned(truckId, userUUID);
            JpaHelper.commitTransaction();
            return hasPlanAssigned;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public Optional<TruckReadOnlyDto> deleteTruck(Long truckId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException {

        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(uuid);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            Optional<TruckJpo> truckJpoOptional = truckDao.getSingleEntityByIdByUserUUID(truckId, uuid);

            if (truckJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No truck found with that id");
            }

            Optional<TruckJpo> deletedTruckJpoOptional = truckDao.deleteEntity(truckId, uuid);
            JpaHelper.commitTransaction();

            if (deletedTruckJpoOptional.isEmpty()) {
                throw new EntityInvalidArgumentException("InvalidArgument", "There was an error in the persist process.");
            }
            Truck truck = truckMapper.truckJpoToTruck(deletedTruckJpoOptional.get());
            TruckReadOnlyDto truckReadOnlyDto = truckMapper.truckToTruckReadOnlyDto(truck);

            return Optional.of(truckReadOnlyDto);

        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }
    }
