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
import org.routing.software.AlgorithmSpec;
import org.routing.software.core.RoleType;
import org.routing.software.dao.*;
import org.routing.software.dtos.*;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.mappers.*;
import org.routing.software.model.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WeldJunit5Extension.class)
@EnableWeld
class PlanServiceImplTest {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

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

    @Inject
    UserServiceImpl userService;

    @Inject
    LocationNodeServiceImpl locationNodeService;

    @Inject
    TruckServiceImpl truckService;

    @Inject
    PlanServiceImpl planService;

    @Inject
    EntityManager em;


    @Test
    void insert_and_retrieve_plan_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        // create a user
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("planner@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> user = userService.registerUser(userRegisterDto);
        assertTrue(user.isPresent());

        // create truck
        TruckInsertDto truckInsertDto = TruckInsertDto.builder()
                .unloadingTime(15L)
                .maxSpeed(80L)
                .capacity(10000L)
                .description("Big Truck")
                .build();

        Optional<TruckReadOnlyDto> truck = truckService.insertTruck(truckInsertDto, user.get().getUuid());
        assertTrue(truck.isPresent());

        // create location node
        LocationNodeInsertDto nodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(5.0)
                .coordinatesY(10.0)
                .capacity(500L)
                .description("Warehouse")
                .build();

        Optional<LocationNodeReadOnlyDto> node = locationNodeService.insertNode(nodeInsertDto, user.get().getUuid());
        assertTrue(node.isPresent());

        // build plan
        PlanInsertDto planInsertDto = PlanInsertDto.builder()
                .trucksList(List.of(truck.get()))
                .locationNodeList(List.of(node.get()))
                .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                .timeslotLength(30L)
                .build();

        Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
        assertTrue(plan.isPresent());

        // fetch plan
        Optional<PlanReadOnlyDto> fetchedPlan = planService.getSinglePlanByIdByUserUUID(plan.get().getId(), user.get().getUuid());
        assertTrue(fetchedPlan.isPresent());
        assertEquals(plan.get().getAlgorithmSpec(), fetchedPlan.get().getAlgorithmSpec());
        assertEquals(1, fetchedPlan.get().getTruckReadOnlyDtoList().size());
        assertEquals(1, fetchedPlan.get().getLocationNodeReadOnlyDtoList().size());
    }

    @Test
    void delete_plan_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        // create a user
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("planner2@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> user = userService.registerUser(userRegisterDto);
        assertTrue(user.isPresent());

        // create truck
        TruckInsertDto truckInsertDto = TruckInsertDto.builder()
                .unloadingTime(10L)
                .maxSpeed(70L)
                .capacity(9000L)
                .description("Small Truck")
                .build();
        Optional<TruckReadOnlyDto> truck = truckService.insertTruck(truckInsertDto, user.get().getUuid());
        assertTrue(truck.isPresent());

        // create node
        LocationNodeInsertDto nodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(false)
                .coordinatesX(15.0)
                .coordinatesY(25.0)
                .capacity(200L)
                .description("Drop Point")
                .build();
        Optional<LocationNodeReadOnlyDto> node = locationNodeService.insertNode(nodeInsertDto, user.get().getUuid());
        assertTrue(node.isPresent());

        // create plan
        PlanInsertDto planInsertDto = PlanInsertDto.builder()
                .trucksList(List.of(truck.get()))
                .locationNodeList(List.of(node.get()))
                .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                .timeslotLength(20L)
                .build();
        Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
        assertTrue(plan.isPresent());

        // delete plan
        Optional<PlanReadOnlyDto> deletedPlan = planService.deletePlan(plan.get().getId(), user.get().getUuid());
        assertTrue(deletedPlan.isPresent());

        // now fetching should fail
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            planService.getSinglePlanByIdByUserUUID(deletedPlan.get().getId(), user.get().getUuid());
        });

        assertEquals("No plan found with that uuid", exception.getMessage());
    }
}
