package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.Session;
import com.springBoot.autoEcole.model.Candidate;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", source = "candidate")
    Session toEntity(Session source, Candidate candidate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    void updateEntity(@MappingTarget Session target, Session source);
}