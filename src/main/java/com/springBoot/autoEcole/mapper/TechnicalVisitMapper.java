package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.TechnicalVisit;
import com.springBoot.autoEcole.model.Vehicle;

@Mapper(componentModel = "spring")
public interface TechnicalVisitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", source = "vehicle")
    TechnicalVisit toEntity(TechnicalVisit source, Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    void updateEntity(@MappingTarget TechnicalVisit target, TechnicalVisit source);
}