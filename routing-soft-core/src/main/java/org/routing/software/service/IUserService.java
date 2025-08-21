package org.routing.software.service;

import org.routing.software.dtos.UserRegisterDto;
import org.routing.software.exceptions.exceptionCategories.AppServerException;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.model.User;

import java.util.Optional;

public interface IUserService {

    Optional<User> registerUser(UserRegisterDto userRegisterDto) throws EntityInvalidArgumentException, EntityAlreadyExistsException;
    Optional<User> getUserByUuid(String uuid) throws EntityNotFoundException;
    Optional<User> getUserByUsername(String username) throws EntityNotFoundException;
    boolean isUserValid(String username, String password);
    boolean isEmailExists(String username);
    boolean isUserExistsAndActive(String username);
    boolean isUserConfirmationTokenValid(String token);

}
