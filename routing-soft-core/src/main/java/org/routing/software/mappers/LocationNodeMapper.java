package org.routing.software.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.routing.software.dtos.LocationNodeInsertDto;
import org.routing.software.dtos.LocationNodeReadOnlyDto;
import org.routing.software.jpos.LocationNodeJpo;
import org.routing.software.model.LocationNode;

/**
 * A mapper for LocationNode Entity mapping from dto -> entity -> jpo and vice versa.
 */

@Mapper(componentModel = "cdi",
        builder = @Builder(disableBuilder = true))
public interface LocationNodeMapper {

    LocationNodeMapper INSTANCE = Mappers.getMapper(LocationNodeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    LocationNode locationNodeInsertDtoToLocationNode(LocationNodeInsertDto locationNodeInsertDto);


    @Mapping(target = "assignmentJpos", ignore = true)
    @Mapping(target = "userJpo", ignore = true)
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "description", source = "description")
    LocationNodeJpo locationNodeToLocationNodeJpo(LocationNode locationNode);

    //from jpo -> entity -> jpo
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "description", source = "description")
    LocationNode locationNodeJpoToLocationNode(LocationNodeJpo locationNodeJpo);

    LocationNodeReadOnlyDto locationNodeToLocationNodeReadOnlyDto(LocationNode locationNode);

    @Mapping(target = "assignmentJpos", ignore = true)
    @Mapping(target = "userJpo", ignore = true)
    LocationNodeJpo locationNodeReadOnlyDtoToLocationNodeJpo(LocationNodeReadOnlyDto locationNodeReadOnlyDto);

}
