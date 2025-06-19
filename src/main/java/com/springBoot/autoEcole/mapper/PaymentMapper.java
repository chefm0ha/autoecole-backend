package com.springBoot.autoEcole.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import com.springBoot.autoEcole.model.Payment;
import com.springBoot.autoEcole.model.Candidate;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", source = "candidate")
    @Mapping(target = "paymentTranches", ignore = true)
    Payment toEntity(Payment source, Candidate candidate);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "paymentTranches", ignore = true)
    void updateEntity(@MappingTarget Payment target, Payment source);
}