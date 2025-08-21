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
import org.routing.software.dtos.*;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(WeldJunit5Extension.class) //we will use weld in order to use the injection on services. IMPORTANT we need a producer!!!
@EnableWeld
class LocationNodeServiceImplTest {

    @WeldSetup
    WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

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
    void insert_and_retrieve_node_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        //create a user
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("test@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> userJpo = userService.registerUser(userRegisterDto);
        assertTrue(userJpo.isPresent());

        // Create location node
        LocationNodeInsertDto nodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(10.0)
                .coordinatesY(20.0)
                .capacity(500L)
                .description("Warehouse")
                .build();

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDto = locationNodeService.insertNode(nodeInsertDto, userJpo.get().getUuid());
        assertTrue(locationNodeReadOnlyDto.isPresent());

        //assert that the conversion is ok
        assertEquals(nodeInsertDto.isSource(), locationNodeReadOnlyDto.get().isSource());
        assertEquals(nodeInsertDto.getCoordinatesX(), locationNodeReadOnlyDto.get().getCoordinatesX());
        assertEquals(nodeInsertDto.getCoordinatesY(), locationNodeReadOnlyDto.get().getCoordinatesY());
        assertEquals(nodeInsertDto.getCapacity(), locationNodeReadOnlyDto.get().getCapacity());
        assertEquals(nodeInsertDto.getDescription(), locationNodeReadOnlyDto.get().getDescription());

        //fetch from db
        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoFetched =
                locationNodeService.getSingleNodeByIdByUserUUID(locationNodeReadOnlyDto.get().getId() , userJpo.get().getUuid());
        assertTrue(locationNodeReadOnlyDtoFetched.isPresent());

        assertEquals(locationNodeReadOnlyDtoFetched.get().getUuid(), locationNodeReadOnlyDto.get().getUuid());
        assertEquals(locationNodeReadOnlyDtoFetched.get().isSource(), locationNodeReadOnlyDto.get().isSource());
        assertEquals(locationNodeReadOnlyDtoFetched.get().getCoordinatesX(), locationNodeReadOnlyDto.get().getCoordinatesX());
        assertEquals(locationNodeReadOnlyDtoFetched.get().getCoordinatesY(), locationNodeReadOnlyDto.get().getCoordinatesY());
        assertEquals(locationNodeReadOnlyDtoFetched.get().getCapacity(), locationNodeReadOnlyDto.get().getCapacity());
        assertEquals(locationNodeReadOnlyDtoFetched.get().getDescription(), locationNodeReadOnlyDto.get().getDescription());
    }

    @Test
    void retrieveMultipleNode_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        //create a user
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("test@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> userJpo = userService.registerUser(userRegisterDto);
        assertTrue(userJpo.isPresent());

        //create a another user
        UserRegisterDto anotherUserRegisterDto = new UserRegisterDto();
        anotherUserRegisterDto.setUsername("koukou@gmail.com");
        anotherUserRegisterDto.setPassword("testPassword");
        anotherUserRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> anotherUserJpo = userService.registerUser(anotherUserRegisterDto);
        assertTrue(anotherUserJpo.isPresent());

        // Create location node
        LocationNodeInsertDto nodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(10.0)
                .coordinatesY(20.0)
                .capacity(500L)
                .description("Warehouse")
                .build();

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDto = locationNodeService.insertNode(nodeInsertDto, userJpo.get().getUuid());
        assertTrue(locationNodeReadOnlyDto.isPresent());

        // Create location node
        LocationNodeInsertDto anotherNodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(10.0)
                .coordinatesY(20.0)
                .capacity(500L)
                .description("Warehouse")
                .build();

        Optional<LocationNodeReadOnlyDto> anotherLocationNodeReadOnlyDto = locationNodeService.insertNode(anotherNodeInsertDto, userJpo.get().getUuid());
        assertTrue(anotherLocationNodeReadOnlyDto.isPresent());

        // Create location node for another user
        LocationNodeInsertDto nodeInsertDtoOfAnotherUser = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(10.0)
                .coordinatesY(20.0)
                .capacity(500L)
                .description("Node15")
                .build();

        Optional<LocationNodeReadOnlyDto> nodeReadOnlyDtoOfAnotherUser = locationNodeService.insertNode(anotherNodeInsertDto, anotherUserJpo.get().getUuid());
        assertTrue(nodeReadOnlyDtoOfAnotherUser.isPresent());

        //fetch both from db (only 2 should be fetched)
        List<LocationNodeReadOnlyDto> readOnlyDtoList =
                locationNodeService.getAllNodesByUserUUID(userJpo.get().getUuid());
        assertEquals(2, readOnlyDtoList.size());

        //fetch both from db (only 2 should be fetched)
        List<LocationNodeReadOnlyDto> readOnlyDtoList2 =
                locationNodeService.getAllNodesByUserUUID(anotherUserJpo.get().getUuid());
        assertEquals(1, readOnlyDtoList2.size());
    }

    @Test
    void deleteNode_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        //create a user
        UserRegisterDto userRegisterDto = new UserRegisterDto();
        userRegisterDto.setUsername("test@gmail.com");
        userRegisterDto.setPassword("testPassword");
        userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> userJpo = userService.registerUser(userRegisterDto);
        assertTrue(userJpo.isPresent());

        //create a another user
        UserRegisterDto anotherUserRegisterDto = new UserRegisterDto();
        anotherUserRegisterDto.setUsername("koukou@gmail.com");
        anotherUserRegisterDto.setPassword("testPassword");
        anotherUserRegisterDto.setRole(RoleType.SIMPLE_USER.name());
        Optional<User> anotherUserJpo = userService.registerUser(anotherUserRegisterDto);
        assertTrue(anotherUserJpo.isPresent());

        // Create location node
        LocationNodeInsertDto nodeInsertDto = LocationNodeInsertDto.builder()
                .isSource(true)
                .coordinatesX(10.0)
                .coordinatesY(20.0)
                .capacity(500L)
                .description("Warehouse")
                .build();

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDto = locationNodeService.insertNode(nodeInsertDto, userJpo.get().getUuid());
        assertTrue(locationNodeReadOnlyDto.isPresent());

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoDeleted =
                locationNodeService.deleteNode(locationNodeReadOnlyDto.get().getId(), userJpo.get().getUuid());

        // Expect EntityAlreadyExistsException on duplicate
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            locationNodeService.getSingleNodeByIdByUserUUID(locationNodeReadOnlyDtoDeleted.get().getId(), userJpo.get().getUuid());
        });

        assertEquals("No location node found with that id", exception.getMessage());
    }

    @Test
    void hasPlanAssigned_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
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

        // build plan
        PlanInsertDto planInsertDto = PlanInsertDto.builder()
                .trucksList(List.of(truck.get()))
                .locationNodeList(List.of(node.get()))
                .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                .timeslotLength(30L)
                .build();

        Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
        assertTrue(plan.isPresent());

        boolean hasPlanAssigned = locationNodeService.hasPlanAssigned(node.get().getId(), user.get().getUuid());
        assertTrue(hasPlanAssigned);
    }

    @Test
    void hasPlanAssigned_failure() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
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

        // build plan
        PlanInsertDto planInsertDto = PlanInsertDto.builder()
                .trucksList(List.of(truck.get()))
                .locationNodeList(new ArrayList<>())
                .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                .timeslotLength(30L)
                .build();


        Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
        assertTrue(plan.isPresent());

        boolean hasPlanAssigned = locationNodeService.hasPlanAssigned(node.get().getId(), user.get().getUuid());
        assertFalse(hasPlanAssigned);
    }

}
