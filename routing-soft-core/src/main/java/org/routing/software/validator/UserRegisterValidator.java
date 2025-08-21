package org.routing.software.validator;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.routing.software.dtos.UserRegisterDto;
import org.routing.software.service.IUserService;
import java.util.HashMap;
import java.util.Map;

/**
 * Validates if the userRegisterJpo is correct:
 * ie 1 - the password is equal with confirmPassword.
 *    2 - the user (email) not exist in db.
 */

@ApplicationScoped
public class UserRegisterValidator {

    //TODO CHECK IMPLEMENTATION OF SERVICE AND DAO ++ IF SINGLETON IS OK
    @Inject
    IUserService userService;

    public UserRegisterValidator() {}

    public <T extends UserRegisterDto>Map<String,String> validateDto(T dto) {

        Map<String, String> errors = new HashMap<>();

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            errors.put("confirmPassword", "Passwords are not the same");
        }

        if (userService.isEmailExists(dto.getUsername())) {
            errors.put("user", "username already exists");
        }
        return errors;
    }
}
