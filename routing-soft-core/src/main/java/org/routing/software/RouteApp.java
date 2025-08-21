package org.routing.software;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import org.routing.software.authentication.CorsFilter;
import org.routing.software.authentication.JwtAuthenticationFilter;
import org.routing.software.rest.AuthRestController;
import org.routing.software.rest.LocationNodeRestController;
import org.routing.software.rest.PlanRestController;
import org.routing.software.rest.TruckRestController;

import java.util.HashSet;
import java.util.Set;


@ApplicationPath("/api")
public class RouteApp extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(CorsFilter.class);
        classes.add(JwtAuthenticationFilter.class);
        classes.add(AuthRestController.class);
        classes.add(LocationNodeRestController.class);
        classes.add(PlanRestController.class);
        classes.add(TruckRestController.class);
        return classes;
    }

}
