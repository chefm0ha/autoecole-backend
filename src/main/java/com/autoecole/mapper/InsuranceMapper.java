package com.autoecole.mapper;

import org.springframework.stereotype.Component;
import com.autoecole.model.Insurance;
import com.autoecole.model.Vehicle;

@Component
public class InsuranceMapper {

    public Insurance toEntity(Insurance source, Vehicle vehicle) {
        if (source == null || vehicle == null) {
            return null;
        }

        Insurance target = new Insurance();
        target.setAmount(source.getAmount());
        target.setCompany(source.getCompany());
        target.setNextOperationDate(source.getNextOperationDate());
        target.setOperationDate(source.getOperationDate());
        target.setVehicle(vehicle);

        return target;
    }

    public void updateEntity(Insurance target, Insurance source) {
        if (source == null || target == null) {
            return;
        }

        target.setAmount(source.getAmount());
        target.setCompany(source.getCompany());
        target.setNextOperationDate(source.getNextOperationDate());
        target.setOperationDate(source.getOperationDate());
        // Note: vehicle is ignored in update
    }
}