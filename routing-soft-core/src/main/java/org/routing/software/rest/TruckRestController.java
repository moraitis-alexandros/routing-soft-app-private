package org.routing.software.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.routing.software.dtos.TruckInsertDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.service.ITruckService;
import org.routing.software.validator.ValidatorUtil;

import java.util.List;
import java.util.Optional;

/**
 * This is the truck controller. Based on the solution provided, no need to update truck
 * due to additional complexity, if a plan is already created. For that reason only soft delete
 * is allowed and that is the case when the truck is not already assigned to some plan.
 * If the user want to update, he will soft delete and recreate the truck from the beginning.
 */
@RequestScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Path("/truck")
public class TruckRestController {

    private final ITruckService truckService;
    private final ValidatorUtil validatorUtil;

    @GET
    @Path("/getAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTrucks(@Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {
        List<TruckReadOnlyDto> truckReadOnlyDtoList = truckService.getAllTrucksByUserUUID(securityContext.getUserPrincipal().getName());
        return Response
                .status(Response.Status.OK)
                .entity(truckReadOnlyDtoList)
                .build();
    }

    @GET
    @Path("/getAll/{plan}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUserTrucksByPlan(@PathParam("plan") Long planId,
                                        @Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {

        List<TruckReadOnlyDto> truckReadOnlyDtoList = truckService.getAllTrucksByPlanIdAndByUserUUID(planId, securityContext.getUserPrincipal().getName());
        return Response.status(Response.Status.OK)
                .entity(truckReadOnlyDtoList)
                .build();
    }

    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertTruck(TruckInsertDto truckInsertDto,
                                @Context UriInfo uriInfo,
                                @Context SecurityContext securityContext)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        //Απο την στιγμη που υπαρχει ο Exception Mapper δεν χρειαζεται να κανω try - catch γιατι θελω να κανω throw αν συμβει καποιο exception
        //και να αναλαβει ο exception mapper
        //Validate insert jpo ->
        List<String> errors = validatorUtil.validate(truckInsertDto);

        if (!errors.isEmpty()) {
            throw new EntityInvalidArgumentException("Truck", String.join(", ", errors));
        }

        Optional<TruckReadOnlyDto> truckReadOnlyDto = truckService.insertTruck(truckInsertDto, securityContext.getUserPrincipal().getName());

            return Response
                    .created(uriInfo
                            .getAbsolutePathBuilder()
                            .path(truckReadOnlyDto.get().getUuid())
                            .build())
                    .entity(truckReadOnlyDto.get())
                    .build();
}

    @POST
    @Path("/delete/{truckId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteTruck(@PathParam("truckId") Long truckId,
                                @Context SecurityContext securityContext)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        if (!truckService.isTruckExists(truckId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("NotFound", "Truck with id: "
                    + truckId + " not found in order to delete it for the user provided.");
        }


        if (truckService.hasPlanAssigned(truckId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("InvalidArgument", "Truck with id: "
                    + truckId + " cannot be deleted because it already has a plan assigned. Delete the plan and then try again");
        }
        //only the user can soft delete its truck
        Optional<TruckReadOnlyDto> truckReadOnlyDto = truckService.deleteTruck(truckId, securityContext.getUserPrincipal().getName());

        if (truckReadOnlyDto.isEmpty()) {
            throw new EntityNotFoundException("InvalidArgument", "Truck with id: "
                    + truckId + " cannot be deleted. There was an error in the process");
        }


        return Response
                .status(Response.Status.OK)
                .entity(truckReadOnlyDto.get())
                .build();

    }

}
