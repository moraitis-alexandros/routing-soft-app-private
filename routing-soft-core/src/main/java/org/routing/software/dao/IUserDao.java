package org.routing.software.dao;

import org.routing.software.jpos.UserJpo;

import java.util.Optional;

public interface IUserDao extends IGenericDao<UserJpo> {

    Optional<UserJpo> getByUsername(String username);
    Optional<UserJpo> getByUuid(String uuid);
    boolean isUserValid(String username, String password);
    boolean isUserExists(String username); //email is same as username
    boolean isUserExistsAndIsActive(String username);
    UserJpo userConfirmationTokenExists(String registrationConfirmationToken);
    Optional<UserJpo> getUserByUuid(String uuid);
    }
