package org.routing.software.dao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.NoResultException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.exceptions.exceptionCategories.MultipleEntitiesExistException;
import org.routing.software.jpos.UserJpo;
import org.routing.software.security.SecUtil;

import java.util.*;

@ApplicationScoped
public class UserDaoImpl extends AbstractDao<UserJpo> implements IUserDao {

    @Override
    public Optional<UserJpo> getByUsername(String username) {
        String query = "SELECT u FROM UserJpo u WHERE u.username = :username";

        try {
            UserJpo user = getEntityManager()
                    .createQuery(query, UserJpo.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            //TODO LOGGER
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserJpo> getByUuid(String uuid) {
        String query = "SELECT u FROM UserJpo u WHERE u.uuid = :uuid";

        try {
            UserJpo user = getEntityManager()
                    .createQuery(query, UserJpo.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            //TODO LOGGER
            return Optional.empty();
        }
    }

    @Override
    public boolean isUserValid(String username, String password) {
        try {
            Optional<UserJpo> userJpo = getByUsername(username);
            return userJpo.filter(jpo -> SecUtil.checkPassword(password, jpo.getPassword())).isPresent();
        } catch (NoResultException e) {
            //TODO LOGGER
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUserExists(String username) {
        String query = "SELECT COUNT(u) FROM UserJpo u WHERE u.username= :username";
        try {
            Long count = getEntityManager().createQuery(query, Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            //TODO LOGGER
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isUserExistsAndIsActive(String username) {
        String query = "SELECT COUNT(u) FROM UserJpo u WHERE u.username= :username and u.isActive=:isActive";
        try {
            Long count = getEntityManager().createQuery(query, Long.class)
                    .setParameter("username", username)
                    .setParameter("isActive", true)
                    .getSingleResult();
            return count > 0;
        } catch (NoResultException e) {
            //TODO LOGGER
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserJpo userConfirmationTokenExists(String registrationConfirmationToken)  {
        String query = "SELECT u FROM UserJpo u WHERE u.confirmationToken=:token";
        try {
            List<UserJpo> userJpoList = new ArrayList<>();

            userJpoList = getEntityManager().createQuery(query, UserJpo.class)
                    .setParameter("token", registrationConfirmationToken)
                    .getResultList();

            //The below case is nearly impossible. It means that a token is found but no user exists, which cannot be done
            //because as soon as the user will be created the token be created too.
            if (userJpoList.isEmpty()) {
                throw new EntityNotFoundException("User", "No corresponding user for provided token");
            }

            //The below case is nearly impossible. It means that a token is found and also multiple users exist.
            //So this indicates a problem with persistence
            if (userJpoList.size() > 1) {
                throw new MultipleEntitiesExistException("User", "Multiple users found for the provided token");
            }

            return userJpoList.get(0);

        } catch (NoResultException | EntityNotFoundException | MultipleEntitiesExistException  e) {
            //TODO LOGGER IS IT CORRECT?
            throw new RuntimeException(e);
        }

    }

    @Override
    public Optional<UserJpo> getUserByUuid(String uuid) {
        String query = "SELECT u FROM UserJpo u WHERE uuid = :uuid";

        try {
            UserJpo user = getEntityManager()
                    .createQuery(query, UserJpo.class)
                    .setParameter("uuid", uuid)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            //TODO LOGGER
            return Optional.empty();
        }
    }

}
