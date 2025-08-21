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
import org.routing.software.TestPersistenceProducer;
import org.routing.software.core.RoleType;
import org.routing.software.dao.TruckDaoImpl;
import org.routing.software.dao.UserDaoImpl;
import org.routing.software.dtos.*;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.mappers.*;
import org.routing.software.model.TspAlgorithm;
import org.routing.software.model.User;
import org.routing.software.security.JwtService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

    @ExtendWith(WeldJunit5Extension.class)
    @EnableWeld
    class TruckServiceImplTest {

        @WeldSetup
        WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

        @Inject
        UserServiceImpl userService;

        @Inject
        TruckServiceImpl truckService;

        @Inject
        PlanServiceImpl planService;

        @Inject
        LocationNodeServiceImpl locationNodeService;

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
        void insert_and_retrieve_truck_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
            // Create user
            UserRegisterDto userRegisterDto = new UserRegisterDto();
            userRegisterDto.setUsername("truckuser@gmail.com");
            userRegisterDto.setPassword("testPassword");
            userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
            Optional<User> userJpo = userService.registerUser(userRegisterDto);
            assertTrue(userJpo.isPresent());

            // Insert truck
            TruckInsertDto truckInsertDto = TruckInsertDto.builder()
                    .unloadingTime(30L)
                    .maxSpeed(80L)
                    .capacity(20000L)
                    .description("Volvo FH16")
                    .build();

            Optional<TruckReadOnlyDto> truckReadOnlyDto = truckService.insertTruck(truckInsertDto, userJpo.get().getUuid());
            assertTrue(truckReadOnlyDto.isPresent());

            // Verify inserted fields
            assertEquals(truckInsertDto.getUnloadingTime(), truckReadOnlyDto.get().getUnloadingTime());
            assertEquals(truckInsertDto.getMaxSpeed(), truckReadOnlyDto.get().getMaxSpeed());
            assertEquals(truckInsertDto.getCapacity(), truckReadOnlyDto.get().getCapacity());
            assertEquals(truckInsertDto.getDescription(), truckReadOnlyDto.get().getDescription());

            // Fetch from DB again
            Optional<TruckReadOnlyDto> truckFetched =
                    truckService.getSingleTruckByIdByUserUUID(truckReadOnlyDto.get().getId(), userJpo.get().getUuid());
            assertTrue(truckFetched.isPresent());
            assertEquals(truckFetched.get().getUuid(), truckReadOnlyDto.get().getUuid());
        }

        @Test
        void retrieve_multiple_trucks_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
            // Create user
            UserRegisterDto userRegisterDto = new UserRegisterDto();
            userRegisterDto.setUsername("multiuser@gmail.com");
            userRegisterDto.setPassword("testPassword");
            userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
            Optional<User> userJpo = userService.registerUser(userRegisterDto);
            assertTrue(userJpo.isPresent());

            // Insert 2 trucks for this user
            TruckInsertDto truck1 = TruckInsertDto.builder()
                    .unloadingTime(20L).maxSpeed(70L).capacity(15000L).description("DAF XF").build();
            TruckInsertDto truck2 = TruckInsertDto.builder()
                    .unloadingTime(25L).maxSpeed(90L).capacity(18000L).description("Scania R").build();

            truckService.insertTruck(truck1, userJpo.get().getUuid());
            truckService.insertTruck(truck2, userJpo.get().getUuid());

            // Another user with 1 truck
            UserRegisterDto anotherUserRegisterDto = new UserRegisterDto();
            anotherUserRegisterDto.setUsername("anothertruck@gmail.com");
            anotherUserRegisterDto.setPassword("testPassword");
            anotherUserRegisterDto.setRole(RoleType.SIMPLE_USER.name());
            Optional<User> anotherUser = userService.registerUser(anotherUserRegisterDto);
            assertTrue(anotherUser.isPresent());

            TruckInsertDto truck3 = TruckInsertDto.builder()
                    .unloadingTime(15L).maxSpeed(60L).capacity(12000L).description("MAN TGX").build();
            truckService.insertTruck(truck3, anotherUser.get().getUuid());

            // Fetch trucks for first user
            List<TruckReadOnlyDto> trucksUser1 = truckService.getAllTrucksByUserUUID(userJpo.get().getUuid());
            assertEquals(2, trucksUser1.size());

            // Fetch trucks for second user
            List<TruckReadOnlyDto> trucksUser2 = truckService.getAllTrucksByUserUUID(anotherUser.get().getUuid());
            assertEquals(1, trucksUser2.size());
        }

        @Test
        void delete_truck_success() throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
            // Create user
            UserRegisterDto userRegisterDto = new UserRegisterDto();
            userRegisterDto.setUsername("deletetruck@gmail.com");
            userRegisterDto.setPassword("testPassword");
            userRegisterDto.setRole(RoleType.SIMPLE_USER.name());
            Optional<User> userJpo = userService.registerUser(userRegisterDto);
            assertTrue(userJpo.isPresent());

            // Insert truck
            TruckInsertDto truckInsertDto = TruckInsertDto.builder()
                    .unloadingTime(40L)
                    .maxSpeed(100L)
                    .capacity(25000L)
                    .description("Mercedes Actros")
                    .build();

            Optional<TruckReadOnlyDto> truckReadOnlyDto = truckService.insertTruck(truckInsertDto, userJpo.get().getUuid());
            assertTrue(truckReadOnlyDto.isPresent());

            // Delete truck
            Optional<TruckReadOnlyDto> deleted = truckService.deleteTruck(truckReadOnlyDto.get().getId(), userJpo.get().getUuid());
            assertTrue(deleted.isPresent());

            // Expect not found after deletion
            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
                truckService.getSingleTruckByIdByUserUUID(deleted.get().getId(), userJpo.get().getUuid());
            });
            assertEquals("No truck found with that id", ex.getMessage());
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
                    .locationNodeList(new ArrayList<>())
                    .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                    .timeslotLength(30L)
                    .build();

            Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
            assertTrue(plan.isPresent());

            boolean hasPlanAssigned = truckService.hasPlanAssigned(truck.get().getId(), user.get().getUuid());
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
                    .trucksList(new ArrayList<>())
                    .locationNodeList(List.of(node.get()))
                    .algorithmSpec(AlgorithmSpec.SimpleTSP.name())
                    .timeslotLength(30L)
                    .build();


            Optional<PlanReadOnlyDto> plan = planService.insertPlan(planInsertDto, user.get().getUuid());
            assertTrue(plan.isPresent());

            boolean hasPlanAssigned = truckService.hasPlanAssigned(truck.get().getId(), user.get().getUuid());
            assertFalse(hasPlanAssigned);
        }
    }

