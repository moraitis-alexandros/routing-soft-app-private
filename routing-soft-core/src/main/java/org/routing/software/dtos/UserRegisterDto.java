package org.routing.software.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRegisterDto {

    //TODO jakarta validation
    private String username;

    private String password;

    private String confirmPassword;

    private String role;

}
