package org.routing.software.authentication;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import lombok.RequiredArgsConstructor;
import org.routing.software.mappers.UserMapper;
import org.routing.software.model.User;
import org.routing.software.security.CustomSecurityContext;
import org.routing.software.security.JwtService;
import org.routing.software.service.IUserService;

import java.io.IOException;

@Provider //in order to have access to internal between endpoint and request arrives at server
@Priority(Priorities.AUTHENTICATION) //each filter can its own priority depending on the apply case
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@RequestScoped
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    private final JwtService jwtService;
    private final IUserService userService;


    @Context //same as inject but puts jakarta injection. ie it inject our implementation CustomSecurityContext
    SecurityContext securityContext; //in order to put the user that will be found by DAO and validated through jwtService


    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        UriInfo uriInfo = containerRequestContext.getUriInfo();

        //if we have /api/auth/register
        //getPath() will return auth/register ie controller path
        String path = uriInfo.getPath();

        if (isPublicPath(path)) {
            return; //no need to filter
        }

        // Skip OPTIONS requests (CORS preflight)
        if (containerRequestContext.getMethod().equalsIgnoreCase("OPTIONS")) {
            return;
        }


        String authorizationHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION); //inside header will have a field AUTHORIZATION -> bearer
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization Header must be provided");
        }

        String token = authorizationHeader.substring(7).trim();

        try {
            String uuid = jwtService.extractSubject(token);

            User user = userService.getUserByUuid(uuid).orElse(null);

            if (user != null && jwtService.isTokenValid(token, user) && !jwtService.isRegistrationToken(token)) {

                securityContext = new CustomSecurityContext(user);
                containerRequestContext.setSecurityContext(securityContext);
            } else {
                //TODO LOGGER
                System.out.println("Token is not valid " + containerRequestContext.getUriInfo());
            }
        }
        catch (Exception e) {
            throw new NotAuthorizedException("Invalid Token");
        }

    }

    private boolean isPublicPath(String path) {
        return path.equals("auth/register") || path.equals("auth/login") || path.equals("auth/confirmRegistration");
    }
}
