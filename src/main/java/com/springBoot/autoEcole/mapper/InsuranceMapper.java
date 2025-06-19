package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.Insurance;
import com.springBoot.autoEcole.model.Vehicle;

@Mapper(componentModel = "spring")
public interface InsuranceMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", source = "vehicle")
    Insurance toEntity(Insurance source, Vehicle vehicle);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vehicle", ignore = true)
    void updateEntity(@MappingTarget Insurance target, Insurance source);
}