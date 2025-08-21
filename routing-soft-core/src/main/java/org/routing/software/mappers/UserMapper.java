package org.routing.software.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import org.routing.software.dtos.UserLoginDto;
import org.routing.software.dtos.UserReadOnlyDto;
import org.routing.software.dtos.UserRegisterDto;
import org.routing.software.jpos.UserJpo;
import org.routing.software.model.User;

/**
 * A mapper for User Entity mapping from dto -> entity -> jpo and vice versa.
 * It also uses Role Mapper to map the role enum to string
 */
@Mapper(uses = RoleMapper.class,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true),
        componentModel = "cdi"
)//the first property implies that -> uses RoleMapper for role enum to string conversion
//the other three properties imply that any field that cannot be mapped will be ignored without error exception
//and also it will just use getters and setters and not builder-based mapping provided by mapstruct
//the last one implies that the Mapper once, will be created it will be injected to cdi, so i can inject it afterwards
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    //Case 1: Register
    //From Dto -> Jpo Converters in register
    @Mapping(source = "role", target = "roleType")
    User userRegisterDtoToUser(UserRegisterDto userRegisterDto);

    //In register it will return a readOnlyDto
    User userJpoToUser(UserJpo userJpo);

    @Mapping(source = "roleType", target = "role")
    UserReadOnlyDto userToUserDto(User user);

    //Case 2: Login
    //From Dto -> Jpo Converters in Login
    @Mapping(target = "roleType", ignore = true)
    User userLoginDtoToUser(UserLoginDto userLoginDto);

    UserJpo userToUserJpo(User user);

    //In login will return a readOnlyDto
    //convert userJpo To User with the method of case 1
    //convert user To UserReadOnlyDto with the method of case 1

}
