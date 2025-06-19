package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.model.Candidate;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", source = "candidate")
    Exam toEntity(Exam source, Candidate candidate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    void updateEntity(@MappingTarget Exam target, Exam source);
}