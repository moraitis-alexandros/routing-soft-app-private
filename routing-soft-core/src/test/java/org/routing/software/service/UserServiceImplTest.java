package org.routing.software.service;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.routing.software.TestPersistenceProducer;
import org.routing.software.core.RoleType;
import org.routing.software.dao.*;
import org.routing.software.dtos.*;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.mappers.RoleMapperImpl;
import org.routing.software.mappers.UserMapperImpl;
import org.routing.software.model.User;
import org.routing.software.security.JwtService;

import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WeldJunit5Extension.class) //we will use weld in order to use the injection on services. IMPORTANT we need a producer!!!
@EnableWeld
class UserServiceImplTest {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject
    UserServiceImpl userService;

    @Inject
    EntityManager em;


    private void clearDatabase() {
        em.getTransaction().begin();
        em.createQuery("DELETE FROM AssignmentJpo").executeUpdate();
        em.createQuery("DELETE FROM LocationNodeJpo").executeUpdate();
        em.createQuery("DELETE FROM TruckJpo").executeUpdate();
        em.createQuery("DELETE FROM PlanJpo").executeUpdate();
        em.createQuery("DELETE FROM UserJpo").executeUpdate();
        em.getTransaction().commit();
    }

    @BeforeEach
    void setUp() {
        clearDatabase();
    }

    @AfterEach
    void tearDown() {
        clearDatabase();
    }


    @Test
    void insertUser_success() throws EntityAlreadyExistsException {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("test@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> userJpo = userService.registerUser(userRegisterDto);
        assertTrue(userJpo.isPresent());
    }

    @Test
    void insertUser_failure_duplicate() throws EntityAlreadyExistsException {
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("test22@gmail.com");
        userRegisterDto.setPassword("test22Password");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> user = userService.registerUser(userRegisterDto);

        UserRegisterDto duplicateUserDto = new UserRegisterDto();
        duplicateUserDto.setUsername("test22@gmail.com");
        duplicateUserDto.setPassword("testPassword");
        duplicateUserDto.setRole(RoleType.SIMPLE_USER.name());

        // Expect EntityAlreadyExistsException on duplicate
        EntityAlreadyExistsException exception = assertThrows(EntityAlreadyExistsException.class, () -> {
            userService.registerUser(duplicateUserDto);
        });

        assertEquals("User already exists", exception.getMessage());
    }
}
