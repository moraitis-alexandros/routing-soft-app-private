package org.routing.software.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import lombok.RequiredArgsConstructor;
import org.routing.software.dtos.LocationNodeInsertDto;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.dtos.PlanInsertDto;
import org.routing.software.dtos.PlanReadOnlyDto;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.jpos.PlanJpo;
import org.routing.software.mappers.PlanMapperImpl;
import org.routing.software.model.MultipleVrpAlgorithm;
import org.routing.software.model.TspAlgorithm;
import org.routing.software.service.ILocationNodeService;
import org.routing.software.service.IPlanService;
import org.routing.software.validator.ValidatorUtil;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;

/**
 * This is the plan controller. Based on the solution provided, no need to update plan
 * due to additional complexity, if a plan is already created. For that reason only soft delete
 * is allowed and that is the case when the plan is not already assigned to some plan.
 * Also when fetching a certain plan it fetches along all the assignments as well as the correlated
 * trucks and location nodes
 */
@RequestScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Path("/plan")
public class PlanRestController {

    private final IPlanService planService;
    private final ValidatorUtil validatorUtil;
    private final TspAlgorithm tspAlgorithm = new TspAlgorithm();
    private final PlanMapperImpl planMapperImpl;

    @GET
    @Path("/getAll")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPlans(@Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {
        List<PlanReadOnlyDto> planReadOnlyDtoList = planService.getAllPlansByUserUUID(securityContext.getUserPrincipal().getName());
        //mapper
        return Response
                .status(Response.Status.OK)
                .entity(planReadOnlyDtoList)
                .build();
    }


    @POST
    @Path("/insert")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response insertPlan(PlanInsertDto planInsertDto,
                               @Context SecurityContext securityContext,
                               @Context UriInfo uriInfo)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        List<String> errors = validatorUtil.validate(planInsertDto);

        if (!errors.isEmpty()) {
            throw new EntityInvalidArgumentException("Plan", String.join(", ", errors));
        }

        Optional<PlanReadOnlyDto> planReadOnlyDto = planService.insertPlan(planInsertDto, securityContext.getUserPrincipal().getName());

            return Response
                    .created(uriInfo
                            .getAbsolutePathBuilder()
                            .path(planReadOnlyDto.get().getUuid())
                            .build())
                    .entity(planReadOnlyDto.get())
                    .build();
}

    @POST
    @Path("/delete/{planId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteNode(@PathParam("planId") Long planId,
                               @Context SecurityContext securityContext)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        //TO DO check if needed to remove it
        if (!planService.isPlanExists(planId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("NotFound", "Plan with id: "
                    + planId + " not found in order to delete it for the user provided.");
        }

        //only the user can soft delete its truck
        Optional<PlanReadOnlyDto> planReadOnlyDto = planService.deletePlan(planId, securityContext.getUserPrincipal().getName());

        if (planReadOnlyDto.isEmpty()) {
            throw new EntityNotFoundException("InvalidArgument", "Plan with id: "
                    + planId + " cannot be deleted. There was an error in the process");
        }

        return Response
                .status(Response.Status.OK)
                .entity(planReadOnlyDto.get())
                .build();
    }

    @POST
    @Path("/execute/{planId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response executePlan(@PathParam("planId") Long planId,
                               @Context SecurityContext securityContext)
            throws EntityNotFoundException, EntityInvalidArgumentException {
        //TO DO check if needed to remove it
        if (!planService.isPlanExists(planId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("NotFound", "Plan with id: "
                    + planId + " not found in order to delete it for the user provided.");
        }

        //only the user can soft delete its truck
        Optional<PlanReadOnlyDto> planReadOnlyDto = planService.deletePlan(planId, securityContext.getUserPrincipal().getName());

        if (planReadOnlyDto.isEmpty()) {
            throw new EntityNotFoundException("InvalidArgument", "Plan with id: "
                    + planId + " cannot be deleted. There was an error in the process");
        }

        return Response
                .status(Response.Status.OK)
                .entity(planReadOnlyDto.get())
                .build();
    }

    @GET
    @Path("/status{planId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkPlanStatus(@Context SecurityContext securityContext) throws EntityNotFoundException, EntityInvalidArgumentException {
        List<PlanReadOnlyDto> planReadOnlyDtoList = planService.getAllPlansByUserUUID(securityContext.getUserPrincipal().getName());
        //mapper
        return Response
                .status(Response.Status.OK)
                .entity(planReadOnlyDtoList)
                .build();
    }

    @GET
    @Path("/solve/{planId}")
    @Consumes
    @Produces
    public Response solve(@Context SecurityContext securityContext,
    @PathParam("planId") Long planId) throws EntityNotFoundException, EntityInvalidArgumentException {

        if (!planService.isPlanExists(planId, securityContext.getUserPrincipal().getName())) {
            throw new EntityNotFoundException("NotFound", "Plan with id: "
                    + planId + " not found in order to delete it for the user provided.");
        }

        Optional<PlanReadOnlyDto> planReadOnlyDto = planService.solvePlan(planId, securityContext.getUserPrincipal().getName());

        return Response
                .status(Response.Status.OK)
                .entity(planReadOnlyDto.get())
                .build();
    }
}
