package org.routing.software.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.routing.software.dtos.LocationNodeInsertDto;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.service.ILocationNodeService;
import org.routing.software.validator.ValidatorUtil;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
/**
 * This is the location node controller. Based on the solution provided, no need to update location node
 * due to additional complexity, if a plan is already created. For that reason only soft delete
 * is allowed and that is the case when the node is not already assigned to some plan.
 * If the user want to update, he will soft delete and recreate the node from the beginning.
 */
@RequestScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Path("/node")
public class LocationNodeRestController {

    private final ILocationNodeService locationNodeService;
    private final ValidatorUtil validatorUtil;

    @GET
    @Path("/getAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserNodes(@Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {
        List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList = locationNodeService.getAllNodesByUserUUID(securityContext.getUserPrincipal().getName());
        return Response
                .status(Response.Status.OK)
                .entity(locationNodeReadOnlyDtoList)
                .build();
    }

    @GET
    @Path("/getAll/{plan}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserNodesByPlan(@PathParam("plan") Long planId,
                                       @Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {
        List<LocationNodeReadOnlyDto> locationNodeReadOnlyDtoList = locationNodeService.getAllNodesByPlanIdAndByUserUUID(planId, securityContext.getUserPrincipal().getName());
        return Response
                .status(Response.Status.OK)
                .entity(locationNodeReadOnlyDtoList)
                .build();
    }

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertNode(LocationNodeInsertDto locationNodeInsertDto,
                               @Context SecurityContext securityContext,
                               @Context UriInfo uriInfo)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        List<String> errors = validatorUtil.validate(locationNodeInsertDto);
        if (!errors.isEmpty()) {
            throw new EntityInvalidArgumentException("Location Node", String.join(", ", errors));
        }

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDto = locationNodeService.insertNode(locationNodeInsertDto, securityContext.getUserPrincipal().getName());

        return Response
                .created(uriInfo
                        .getAbsolutePathBuilder()
                        .path(locationNodeReadOnlyDto.get().getUuid())
                        .build())
                .entity(locationNodeReadOnlyDto.get())
                .build();
    }

    @POST
    @Path("/delete/{nodeId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNode(@PathParam("nodeId") Long nodeId,
                               @Context SecurityContext securityContext)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        if (!locationNodeService.isNodeExists(nodeId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("NotFound", "Location Node with id: "
                    + nodeId + " not found in order to delete it for the user provided.");
        }

        if (locationNodeService.hasPlanAssigned(nodeId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("InvalidArgument", "Location Node with id: "
                    + nodeId + " cannot be deleted because it already has a plan assigned. Delete the plan and then try again");
        }

        Optional<LocationNodeReadOnlyDto> locationNodeReadOnlyDto = locationNodeService.deleteNode(nodeId, securityContext.getUserPrincipal().getName());

        if (locationNodeReadOnlyDto.isEmpty()) {
            throw new EntityNotFoundException("InvalidArgument", "Location Node with id: "
                    + nodeId + " cannot be deleted. There was an error in the process");
        }

        return Response
                .status(Response.Status.OK)
                .entity(locationNodeReadOnlyDto.get())
                .build();
    }
}
