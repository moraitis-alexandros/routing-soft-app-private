package org.routing.software.authentication;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.routing.software.dtos.UserLoginDto;
import org.routing.software.security.JwtService;
import org.routing.software.service.IUserService;

@ApplicationScoped
public class AuthenticationProvider {

    @Inject
    private IUserService userService;

    @Inject
    private JwtService jwtService;

    public boolean authenticate(UserLoginDto dto) {
        return userService.isUserValid(dto.getUsername(), dto.getPassword());
    }

    public boolean confirmRegistration(String token) {
        boolean isUserConfirmationTokenValid = false;
        return isUserConfirmationTokenValid = userService.isUserConfirmationTokenValid(token);
    }

}
