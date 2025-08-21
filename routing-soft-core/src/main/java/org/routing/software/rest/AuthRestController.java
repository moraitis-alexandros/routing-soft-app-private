package org.routing.software.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import org.routing.software.authentication.AuthenticationProvider;
import org.routing.software.authentication.AuthenticationResponseDto;
import org.routing.software.dtos.UserLoginDto;
import org.routing.software.dtos.UserReadOnlyDto;
import org.routing.software.dtos.UserRegisterDto;
import org.routing.software.exceptions.exceptionCategories.AppServerException;
import org.routing.software.exceptions.exceptionCategories.EntityAlreadyExistsException;
import org.routing.software.exceptions.exceptionCategories.EntityInvalidArgumentException;
import org.routing.software.exceptions.exceptionCategories.EntityNotFoundException;
import org.routing.software.mappers.UserMapper;
import org.routing.software.model.User;
import org.routing.software.security.JwtService;
import org.routing.software.service.IUserService;
import org.routing.software.validator.UserRegisterValidator;
import org.routing.software.validator.ValidatorUtil;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestScoped
@RequiredArgsConstructor(onConstructor = @__(@Inject))
@Path("/auth")
public class AuthRestController {

//    @OPTIONS
//    @Path("{path: .*}")
//    public Response options() {
//        return Response.ok().build();
//    }

    //TODO
    private final IUserService userService;
    /// it belongs to onCOnstructor due to its final - also applies in nonull
    private final AuthenticationProvider authenticationProvider;
    /// it belongs to onCOnstructor due to its final - also applies in nonull
    private final JwtService jwtService; //it belongs to onCOnstructor due to its final - also applies in nonull
    private final ValidatorUtil validatorUtil;
    private final UserRegisterValidator userRegisterValidator;
    private final UserMapper userMapper;

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(UserRegisterDto userRegisterDto, @Context UriInfo uriInfo) throws EntityInvalidArgumentException,
            EntityAlreadyExistsException, AppServerException {

        List<String> beanErrors = validatorUtil.validate(userRegisterDto);

        //syntax errors
        if (!beanErrors.isEmpty()) {
            throw new EntityInvalidArgumentException("User", String.join(", ", beanErrors)); //the AppExceptionMapper will forward the exception.
        }

        //logic errors
        Map<String, String> otherErrors = userRegisterValidator.validateDto(userRegisterDto);
        if (!otherErrors.isEmpty()) {
            throw new EntityInvalidArgumentException("User", otherErrors.toString()); //the AppExceptionMapper will forward the exception.
        }

        Optional<User> userOptional = userService.registerUser(userRegisterDto);

        if (userOptional.isEmpty()) {
            throw new EntityAlreadyExistsException("User", "The email provided already exists."); //the AppExceptionMapper will forward the exception.
        }

        UserReadOnlyDto userReadOnlyDto = userMapper.userToUserDto(userOptional.get()); //we assured above that optional is not empty
        return Response.created(uriInfo.getAbsolutePathBuilder()
                        .path(userReadOnlyDto
                                .getUuid()) //we forward the uuid not id for security reasons
                        .build())
                .entity(userReadOnlyDto).build();
    }

    //Principal is logged in user
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserLoginDto userLoginDTO, @Context Principal principal) throws EntityNotFoundException {

        boolean isUserValid = authenticationProvider.authenticate(userLoginDTO);

        //check if user exists
        if (!isUserValid) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        //get user, we already checked that user exists (above).
        Optional<User> userOptional = userService.getUserByUsername(userLoginDTO.getUsername());

        String role = userOptional.get().getRoleType().name();
        String token = jwtService.generateToken(userOptional.get().getUuid(), role, "login");
        AuthenticationResponseDto responseDTO = new AuthenticationResponseDto(token);
        return Response.status(Response.Status.OK).entity(responseDTO).build();
    }

//    //Principal is logged in user
//    @GET
//    @Path("/confirmRegistration/{confirmationToken}")
//    @Consumes(MediaType.APPLICATION_JSON)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response confirm(@PathParam("confirmationToken") String confirmationToken) throws EntityNotFoundException {
//
//        boolean isConfirmRegistrationValid = authenticationProvider.confirmRegistration(confirmationToken);
//
//        //check if the user with that token is found && the token is valid (from jwt service)
//        if (!isConfirmRegistrationValid) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        boolean isRegistrationToken = jwtService.isRegistrationToken(confirmationToken);
//        //if it is registration token, then set user attribute active into true
//
//
//        //get user, we already checked that user exists (above).
//        Optional<UserReadOnlyDto> userReadOnlyDtoOptional = userService.getUserByUsername(userLoginDTO.getUsername());
//        String role = userReadOnlyDtoOptional.get().getRole();
//        String token = jwtService.generateToken(userLoginDTO.getUsername(), role);
//        AuthenticationResponseDto responseDTO = new AuthenticationResponseDto(token);
//        return Response.status(Response.Status.OK).entity(responseDTO).build();
////    }
//    }


}

