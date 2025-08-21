package org.routing.software.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserReadOnlyDto {

    private String uuid;

    private String username;

    private String password;

    private String role;

}
