package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.OilChange;
import com.springBoot.autoEcole.model.Vehicle;

@Mapper(componentModel = "spring")
public interface OilChangeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", source = "vehicle")
    OilChange toEntity(OilChange source, Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    void updateEntity(@MappingTarget OilChange target, OilChange source);
}