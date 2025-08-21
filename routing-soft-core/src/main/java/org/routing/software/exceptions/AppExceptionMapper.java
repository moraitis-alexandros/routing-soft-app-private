package org.routing.software.exceptions;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.routing.software.exceptions.exceptionCategories.*;

/**
 * An exception mapper that implements the jakarta exception mapper for EntityGenericException
 * Based on yje instance of the exception provided it sends a certain response status & message
 * For that reason the controller does not need else statement in the exception because
 * the exception mapper is in charged to forward the exception.
 */
@Provider
public class AppExceptionMapper implements ExceptionMapper<EntityGenericException> {

    @Override
    public Response toResponse(EntityGenericException exception) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;

        if (exception instanceof EntityNotFoundException) {
            status = Response.Status.NOT_FOUND;
        } else if (exception instanceof EntityInvalidArgumentException) {
            status = Response.Status.BAD_REQUEST;
        } else if (exception instanceof EntityNotAuthorizedException) {
            status = Response.Status.UNAUTHORIZED;
        } else if (exception instanceof EntityAlreadyExistsException) {
            status = Response.Status.CONFLICT;
        } else if (exception instanceof AppServerException) {
            status = Response.Status.SERVICE_UNAVAILABLE;
        }

        return Response
                .status(status)
                .entity(new ResponseMessageDto(exception.getCode(), exception.getMessage()))
                .type(MediaType.APPLICATION_JSON_TYPE)
                // Add CORS headers so Angular never gets blocked
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }
}
