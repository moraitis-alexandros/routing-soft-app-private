package org.routing.software.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import org.routing.software.dao.ILocationNodeDao;
import org.routing.software.dao.IUserDao;
import org.routing.software.dtos.LocationNodeInsertDto;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.jpos.LocationNodeJpo;
import org.routing.software.jpos.UserJpo;
import org.routing.software.mappers.LocationNodeMapper;
import org.routing.software.model.LocationNode;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class LocationNodeServiceImpl implements ILocationNodeService{

    private final ILocationNodeDao locationNodeDao;
    private final IUserDao userDao;
    private final LocationNodeMapper locationNodeMapper;

    @Override
    public Optional<LocationNodeReadOnlyDto> insertNode(LocationNodeInsertDto locationNodeInsertDto, String userUUID) throws EntityInvalidArgumentException, EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            UserJpo userJpo = userJpoOptional.get();
            LocationNode locationNode = locationNodeMapper.locationNodeInsertDtoToLocationNode(locationNodeInsertDto);
            LocationNodeJpo locationNodeJpo = locationNodeMapper.locationNodeToLocationNodeJpo(locationNode);
            userJpo.addObject(locationNodeJpo);
            locationNodeJpo.setUserJpo(userJpo);
            Optional<LocationNodeJpo> persistedNodeJpo = locationNodeDao.insert(locationNodeJpo);
            userDao.update(userJpo);
            JpaHelper.commitTransaction();

            if (persistedNodeJpo.isPresent()) {
                LocationNode persistedLocationNode = locationNodeMapper.locationNodeJpoToLocationNode(persistedNodeJpo.get());
                LocationNodeReadOnlyDto locationNodeReadOnlyDto = locationNodeMapper.locationNodeToLocationNodeReadOnlyDto(persistedLocationNode);
                return Optional.of(locationNodeReadOnlyDto);
            } else {
                throw new EntityInvalidArgumentException("InvalidArgument", "There was an error in the persist process.");
            }

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public List<LocationNodeReadOnlyDto> getAllNodesByUserUUID(String userUUID) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            List<LocationNodeJpo> locationNodeJpoList = locationNodeDao.getAllEntitiesByUserUUID(userUUID);
            JpaHelper.commitTransaction();

            return locationNodeJpoList.stream()
                    .map(locationNodeMapper::locationNodeJpoToLocationNode)
                    .map(locationNodeMapper::locationNodeToLocationNodeReadOnlyDto)
                    .toList();

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public Optional<LocationNodeReadOnlyDto> getSingleNodeByIdByUserUUID(Long nodeId, String userUUID) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            Optional<LocationNodeJpo> locationNodeJpoOptional = locationNodeDao.getSingleEntityByIdByUserUUID(nodeId, userUUID);
            JpaHelper.commitTransaction();

            if (locationNodeJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No location node found with that id");
            }

            LocationNode locationNode = locationNodeMapper.locationNodeJpoToLocationNode(locationNodeJpoOptional.get());
            LocationNodeReadOnlyDto locationNodeReadOnlyDto = locationNodeMapper.locationNodeToLocationNodeReadOnlyDto(locationNode);

            return Optional.of(locationNodeReadOnlyDto);

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public List<LocationNodeReadOnlyDto> getAllNodesByPlanIdAndByUserUUID(Long planId, String userUUID) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(userUUID);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            List<LocationNodeJpo> locationNodeJpoList = locationNodeDao.getAllEntitiesByPlanIdAndByUserUUID(planId, userUUID);

            if (locationNodeJpoList.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No location node found with that plan id");
            }

            JpaHelper.commitTransaction();

            return locationNodeJpoList.stream()
                    .map(locationNodeMapper::locationNodeJpoToLocationNode)
                    .map(locationNodeMapper::locationNodeToLocationNodeReadOnlyDto)
                    .toList();

        } catch (EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean isNodeExists(Long nodeId, String userUUID) {
        try {
            JpaHelper.beginTransaction();
            boolean exists = locationNodeDao.isEntityExists(nodeId, userUUID);
            JpaHelper.commitTransaction();
            return exists;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean hasPlanAssigned(Long nodeId, String userUUID) {
        try {
            JpaHelper.beginTransaction();
            boolean assigned = locationNodeDao.hasPlanAssigned(nodeId, userUUID);
            JpaHelper.commitTransaction();
            return assigned;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public Optional<LocationNodeReadOnlyDto> deleteNode(Long nodeId, String uuid) throws EntityInvalidArgumentException, EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpoOptional = userDao.getUserByUuid(uuid);

            if (userJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No user found with that uuid");
            }

            Optional<LocationNodeJpo> locationNodeJpoOptional = locationNodeDao.getSingleEntityByIdByUserUUID(nodeId, uuid);

            if (locationNodeJpoOptional.isEmpty()) {
                throw new EntityNotFoundException("NotFound", "No location node found with that id");
            }

            Optional<LocationNodeJpo> deletedNodeJpoOptional = locationNodeDao.deleteEntity(nodeId, uuid);
            JpaHelper.commitTransaction();

            if (deletedNodeJpoOptional.isEmpty()) {
                throw new EntityInvalidArgumentException("InvalidArgument", "There was an error in the persist process.");
            }

            LocationNode locationNode = locationNodeMapper.locationNodeJpoToLocationNode(deletedNodeJpoOptional.get());
            LocationNodeReadOnlyDto locationNodeReadOnlyDto = locationNodeMapper.locationNodeToLocationNodeReadOnlyDto(locationNode);

            return Optional.of(locationNodeReadOnlyDto);

        } catch (EntityInvalidArgumentException | EntityNotFoundException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }
}
