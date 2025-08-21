package org.routing.software.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.ext.Provider;
import org.routing.software.core.RoleType;
import org.routing.software.dao.IUserDao;
import org.routing.software.dtos.UserReadOnlyDto;
import org.routing.software.dtos.UserRegisterDto;
import org.routing.software.exceptions.exceptionCategories.AppServerException;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.jpos.UserJpo;
import org.routing.software.mappers.UserMapper;
import org.routing.software.model.User;
import org.routing.software.security.JwtService;
import org.routing.software.security.SecUtil;

import java.util.Optional;

@ApplicationScoped
public class UserServiceImpl implements IUserService {

    @Inject
    IUserDao userDao;

    @Inject
    JwtService jwtService;

    @Inject
    UserMapper userMapper;

    @Override
    public Optional<User> registerUser(UserRegisterDto userRegisterDto) throws EntityAlreadyExistsException {

        try {
            JpaHelper.beginTransaction();

            Optional<UserJpo> userJpo = userDao.getByUsername(userRegisterDto.getUsername());

            if (userJpo.isPresent()) {
                throw new EntityAlreadyExistsException("User", "User already exists");
            } else {

                User user = userMapper.userRegisterDtoToUser(userRegisterDto);
                UserJpo userJpoForPersist = userMapper.userToUserJpo(user);
                userJpoForPersist.setPassword(SecUtil.hashPassword(userRegisterDto.getPassword()));
                Optional<UserJpo> userJpoOptional = userDao.insert(userJpoForPersist);
                JpaHelper.commitTransaction();

                User persistedUser = userMapper.userJpoToUser(userJpoOptional.get());
                return Optional.of(persistedUser);

            }
        }  catch (EntityAlreadyExistsException e) {
            JpaHelper.rollbackTransaction();
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }


    @Override
    public Optional<User> getUserByUuid(String uuid) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpo = userDao.getUserByUuid(uuid);
            User user = new User();
            if (userJpo.isPresent()) {
                user = userMapper.userJpoToUser(userJpo.get());
                JpaHelper.commitTransaction();
            } else {
                throw new EntityNotFoundException("User", "User with uuid: " + uuid + " not found.");
            }
            return Optional.of(user);

        } catch (EntityNotFoundException e) {
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }

    }

    @Override
    public Optional<User> getUserByUsername(String username) throws EntityNotFoundException {
        try {
            JpaHelper.beginTransaction();
            Optional<UserJpo> userJpo = userDao.getByUsername(username);
            User user = new User();
            if (userJpo.isPresent()) {
                user = userMapper.userJpoToUser(userJpo.get());
                JpaHelper.commitTransaction();
            } else {
                throw new EntityNotFoundException("User", "User with username: " + username + " not found.");
            }
            return Optional.of(user);

        } catch (EntityNotFoundException e) {
            throw e;
        } finally {
            JpaHelper.closeEntityManager();
        }

    }

    @Override
    public boolean isUserValid(String username, String password) {
        try {
            JpaHelper.beginTransaction();
            boolean isUserValid = userDao.isUserValid(username, password);
            JpaHelper.commitTransaction();
            return isUserValid;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean isEmailExists(String username) {
        try {
            JpaHelper.beginTransaction();
            boolean isEmailExists = userDao.isUserExists(username);
            JpaHelper.commitTransaction();
            return isEmailExists;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean isUserExistsAndActive(String username) {
        try {
            JpaHelper.beginTransaction();
            boolean isUserExistsAndIsActive = userDao.isUserExistsAndIsActive(username);
            JpaHelper.commitTransaction();
            return isUserExistsAndIsActive;
        } finally {
            JpaHelper.closeEntityManager();
        }
    }

    @Override
    public boolean isUserConfirmationTokenValid(String token) {
        try {
          JpaHelper.beginTransaction();
            UserJpo userJpo = userDao.userConfirmationTokenExists(token);
            User user = userMapper.userJpoToUser(userJpo);
            boolean isTokenValid = jwtService.isTokenValid(token, user);
            JpaHelper.commitTransaction();
            return isTokenValid;
        } finally {
            JpaHelper.closeEntityManager();
        }
        }
}
