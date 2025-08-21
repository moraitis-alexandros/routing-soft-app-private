package org.routing.software.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.routing.software.dtos.PlanReadOnlyDto;
import org.routing.software.jpos.PlanJpo;

import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "cdi",
        builder = @org.mapstruct.Builder(disableBuilder = true))
public interface PlanMapper {

    @Mapping(target = "createdAt", source = "createdAt", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "locationNodeReadOnlyDtoList", ignore = true)
    @Mapping(target = "truckReadOnlyDtoList", ignore = true)
    @Mapping(target = "routeDtoList", ignore = true)
    PlanReadOnlyDto planJpoToPlanReadOnlyDto(PlanJpo planJpo);

    @Named("localDateTimeToString")
    default String localDateTimeToString(java.time.LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : null;
    }
}
