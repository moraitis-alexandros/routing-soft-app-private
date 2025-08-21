package org.routing.software.mappers;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.routing.software.dtos.TruckInsertDto;
import org.routing.software.dtos.TruckReadOnlyDto;
import org.routing.software.jpos.AssignmentJpo;
import org.routing.software.jpos.TruckJpo;
import org.routing.software.jpos.UserJpo;
import org.routing.software.model.Truck;

import java.util.List;

/**
 * A mapper for Truck Entity mapping from dto -> entity -> jpo and vice versa.
 */

@Mapper(componentModel = "cdi",
builder = @Builder(disableBuilder = true))
public interface TruckMapper {

    TruckMapper INSTANCE = Mappers.getMapper(TruckMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "convertedMaxSpeed", ignore = true)
    @Mapping(target = "convertedUnloadingTime", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    Truck truckInsertDtoToTruck(TruckInsertDto truckInsertDto);


    @Mapping(target = "assignmentJpos", ignore = true)
    @Mapping(target = "userJpo", ignore = true)
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "description", source = "description")
    TruckJpo truckToTruckJpo(Truck truck);

    //from jpo -> entity -> jpo
    @Mapping(target = "convertedMaxSpeed", ignore = true)
    @Mapping(target = "convertedUnloadingTime", ignore = true)
    @Mapping(target = "capacity", source = "capacity")
    @Mapping(target = "description", source = "description")
    Truck truckJpoToTruck(TruckJpo truckJpo);

    TruckReadOnlyDto truckToTruckReadOnlyDto(Truck truck);

    @Mapping(target = "assignmentJpos", ignore = true)
    @Mapping(target = "userJpo", ignore = true)
    TruckJpo truckReadOnlyDtoToTruckJpo(TruckReadOnlyDto truckReadOnlyDto);

}
