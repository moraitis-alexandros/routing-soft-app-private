package org.routing.software.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserLoginDto {

    private String username;

    private String password;

    //no need for role in login
}
