package org.routing.software;

import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import org.routing.software.dao.IPlanDao;
import org.routing.software.dao.PlanDaoImpl;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.dtos.PlanReadOnlyDto;
import org.routing.software.dtos.RouteDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.mappers.PlanMapper;
import org.routing.software.mappers.PlanMapperImpl;
import org.routing.software.model.MultipleVrpAlgorithm;
import org.routing.software.model.TspAlgorithm;
import org.routing.software.service.IMapClient;
import org.routing.software.service.OSMRClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class Main {



    public static void main(String[] args) throws IOException, InterruptedException {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("routingPU");
//        EntityManager em = emf.createEntityManager();
//
//        IPlanDao planDao = new PlanDaoImpl();
//
//        MultipleVrpAlgorithm algorithm = new MultipleVrpAlgorithm();
//        Optional<PlanJpo> planJpo = planDao.getSingleEntityByIdByUserUUID(1, "21c4e41b-e3e4-4b32-8bb1-b1ad8d4d191c");
//
//        if (planJpo.isEmpty()) {
//            System.out.println("Cannot find jpo");
//        }
//
//        PlanMapper planMapper = new PlanMapperImpl();
//        PlanReadOnlyDto planReadOnlyDto = planMapper.planJpoToPlanReadOnlyDto(planJpo.get());
//        PlanReadOnlyDto planReadOnlyDtoSolved = algorithm.solve(planReadOnlyDto);
//
//
//        planReadOnlyDtoSolved.getRouteDtoList().forEach(route -> {
//            route.getStops()
//                    .forEach(stop ->
//                            System.out.println(stop.getDescription()));
//        });


//        mapClient.fetchRouteObject("38.073641252194726", "23.812862375972486",
//                "38.00438534325845", "23.682744511209197");


//

//         --- Create location nodes ---
        LocationNodeReadOnlyDto sourceNode = LocationNodeReadOnlyDto.builder()
                .id(1L).isSource(true)
                .coordinatesX(13.4050).coordinatesY(52.5200)
                .capacity(0L).description("Berlin").uuid("truck-1").build();

        LocationNodeReadOnlyDto paris = LocationNodeReadOnlyDto.builder()
                .id(2L).isSource(false)
                .coordinatesX(2.3522).coordinatesY(48.8566)
                .capacity(20L).description("Paris").uuid("truck-2").build();

        LocationNodeReadOnlyDto rome = LocationNodeReadOnlyDto.builder()
                .id(3L).isSource(false)
                .coordinatesX(12.4964).coordinatesY(41.9028)
                .capacity(30L).description("Rome").uuid("truck-3").build();

        LocationNodeReadOnlyDto athens = LocationNodeReadOnlyDto.builder()
                .id(4L)
                .isSource(false)
                .coordinatesX(23.7275)   // longitude
                .coordinatesY(37.9838)   // latitude
                .capacity(50L)
                .description("Athens")
                .uuid("truck-4")
                .build();

        LocationNodeReadOnlyDto belgrade = LocationNodeReadOnlyDto.builder()
                .id(5L)
                .isSource(false)
                .coordinatesX(20.4573)   // longitude
                .coordinatesY(44.8176)   // latitude
                .capacity(20L)
                .description("Belgrade")
                .uuid("truck-5")
                .build();


        List<LocationNodeReadOnlyDto> nodes = Arrays.asList(sourceNode, paris, rome, athens, belgrade);

        // --- Create trucks ---
        TruckReadOnlyDto truck1 = TruckReadOnlyDto.builder()
                .id(1L)
                .description("Truck 1")
                .capacity(100L)
                .build();

        TruckReadOnlyDto truck2 = TruckReadOnlyDto.builder()
                .id(2L)
                .description("Truck 2")
                .capacity(20L)
                .build();

        List<TruckReadOnlyDto> trucks = Arrays.asList(truck1
                ,
                truck2
        );

        // --- Create plan ---
        PlanReadOnlyDto plan = PlanReadOnlyDto.builder()
                .locationNodeReadOnlyDtoList(nodes)
                .truckReadOnlyDtoList(trucks)
                .build();

        // --- Solve VRP ---
        TspAlgorithm tspAlgorithm = new TspAlgorithm();
        PlanReadOnlyDto planReadOnlyDto = tspAlgorithm.solve(plan);

        System.out.println("\n--- VRP Result ---");
        System.out.println(planReadOnlyDto);

        // Print routes
        if (plan.getRouteDtoList() != null) {
            for (RouteDto route : plan.getRouteDtoList()) {
                System.out.println("Truck " + route.getTruckDescription() + " route:");
                if (route.getStops() != null) {
                    route.getStops().forEach(stop -> System.out.println(" - " + stop.getDescription()));
                }
            }
        }
    }
}

